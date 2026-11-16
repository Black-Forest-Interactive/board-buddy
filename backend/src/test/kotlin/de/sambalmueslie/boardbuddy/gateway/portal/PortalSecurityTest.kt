package de.sambalmueslie.boardbuddy.gateway.portal

import de.sambalmueslie.boardbuddy.core.game.GameService
import de.sambalmueslie.boardbuddy.core.game.api.Game
import de.sambalmueslie.boardbuddy.core.game.api.GameChangeRequest
import de.sambalmueslie.boardbuddy.core.nation.NationService
import de.sambalmueslie.boardbuddy.core.nation.api.Nation
import de.sambalmueslie.boardbuddy.core.nation.api.NationChangeRequest
import de.sambalmueslie.boardbuddy.core.ruleset.RuleSetService
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSet
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSetChangeRequest
import de.sambalmueslie.boardbuddy.core.unit.UnitDefinitionService
import de.sambalmueslie.boardbuddy.core.unit.api.PointsRange
import de.sambalmueslie.boardbuddy.core.unit.api.UnitDefinitionChangeRequest
import de.sambalmueslie.boardbuddy.engine.api.UnitType
import de.sambalmueslie.boardbuddy.gateway.portal.security.PortalPlayerCookie
import io.micronaut.context.annotation.Property
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.MutableHttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@MicronautTest
@Property(name = "micronaut.security.enabled", value = "true")
@Property(name = "micronaut.security.oauth2.enabled", value = "false")
class PortalSecurityTest {

    @Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Inject lateinit var gameService: GameService
    @Inject lateinit var ruleSetService: RuleSetService
    @Inject lateinit var nationService: NationService
    @Inject lateinit var unitDefinitionService: UnitDefinitionService

    private lateinit var game: Game
    private lateinit var ruleSet: RuleSet
    private lateinit var nations: List<Nation>

    @BeforeEach
    fun setup() {
        var rs = ruleSetService.create(RuleSetChangeRequest("security-test"))
        val inf = unitDefinitionService.create(UnitDefinitionChangeRequest("inf", UnitType.INFANTRY, UnitType.MOUNTED, PointsRange(1, 3), PointsRange(1, 3), 4))
        rs = ruleSetService.assignUnitDefinition(rs, inf)!!
        nations = (1..3).map { nationService.create(NationChangeRequest("sec-nation-$it", "desc", "")) }
        nations.forEach { rs = ruleSetService.assignNation(rs, it)!! }
        ruleSet = rs
        game = gameService.assignRuleSet(gameService.create(GameChangeRequest("security-test", "desc")), ruleSet)!!
    }

    @Test
    fun `player token is issued as HttpOnly cookie and resolves to the player`() {
        val response = client.toBlocking().exchange(HttpRequest.POST("/api/portal/player", mapOf("name" to "alice")), Map::class.java)
        val setCookie = response.headers.getAll("Set-Cookie").first { it.startsWith(PortalPlayerCookie.NAME + "=") }
        assertTrue(setCookie.contains("HttpOnly", ignoreCase = true))
        assertTrue(setCookie.contains("Secure", ignoreCase = true))
        assertTrue(setCookie.contains("SameSite=Lax", ignoreCase = true))

        val token = setCookie.substringAfter("=").substringBefore(";")
        val me = client.toBlocking().retrieve(HttpRequest.GET<Any>("/api/portal/player/me").withPlayer(token), Map::class.java)
        assertEquals("alice", me["name"])
    }

    @Test
    fun `portal routes reject anonymous and forged identities`() {
        assertStatus(HttpStatus.UNAUTHORIZED) { client.toBlocking().exchange(HttpRequest.GET<Any>("/api/portal/player/me"), Map::class.java) }
        assertStatus(HttpStatus.UNAUTHORIZED) { client.toBlocking().exchange(HttpRequest.GET<Any>("/api/portal/session"), Any::class.java) }
        // the old numeric id cookie must no longer authenticate
        assertStatus(HttpStatus.UNAUTHORIZED) {
            client.toBlocking().exchange(HttpRequest.GET<Any>("/api/portal/session").cookie(io.micronaut.http.cookie.Cookie.of("player-id", "1")), Any::class.java)
        }
        assertStatus(HttpStatus.UNAUTHORIZED) {
            client.toBlocking().exchange(HttpRequest.GET<Any>("/api/portal/session").withPlayer("not-a-valid-token"), Any::class.java)
        }
        assertStatus(HttpStatus.UNAUTHORIZED) { client.toBlocking().exchange(HttpRequest.GET<Any>("/api/admin/game"), Any::class.java) }
    }

    @Test
    fun `non participants cannot access a session and actions are bound to the caller`() {
        val host = createPlayer("host")
        val other = createPlayer("other")

        val workflow = client.toBlocking().retrieve(
            HttpRequest.POST("/api/portal/session", mapOf("name" to "s", "gameId" to game.id, "ruleSetId" to ruleSet.id, "nationId" to nations[0].id)).withPlayer(host),
            Map::class.java
        )
        val key = workflow["id"] as String

        // not yet joined
        listOf("", "/participants", "/my-info", "/battle", "/technology-status").forEach { suffix ->
            assertStatus(HttpStatus.FORBIDDEN, suffix) {
                client.toBlocking().exchange(HttpRequest.GET<Any>("/api/portal/workflow/$key$suffix").withPlayer(other), Any::class.java)
            }
        }
        assertStatus(HttpStatus.FORBIDDEN) {
            client.toBlocking().exchange(HttpRequest.POST("/api/portal/workflow/$key/unit", mapOf("unitTypeId" to ruleSet.unitDefinitions.first().id)).withPlayer(other), Any::class.java)
        }

        // the join page may list nations before joining
        val nationsResponse = client.toBlocking().exchange(HttpRequest.GET<Any>("/api/portal/workflow/$key/nations").withPlayer(other), Any::class.java)
        assertEquals(HttpStatus.OK, nationsResponse.status)

        client.toBlocking().exchange(HttpRequest.POST("/api/portal/session/join/$key", mapOf("nationId" to nations[1].id)).withPlayer(other), Map::class.java)
        val joined = client.toBlocking().exchange(HttpRequest.GET<Any>("/api/portal/workflow/$key").withPlayer(other), Map::class.java)
        assertEquals(HttpStatus.OK, joined.status)

        // a unit created by "other" is always assigned to "other", even when the old body field is sent
        val unitTypeId = ruleSet.unitDefinitions.first().id
        client.toBlocking().exchange(
            HttpRequest.POST("/api/portal/workflow/$key/unit", mapOf("unitTypeId" to unitTypeId, "playerId" to -1)).withPlayer(other),
            Map::class.java
        )
        val hostInfo = client.toBlocking().retrieve(HttpRequest.GET<Any>("/api/portal/workflow/$key/my-info").withPlayer(host), Map::class.java)
        val otherInfo = client.toBlocking().retrieve(HttpRequest.GET<Any>("/api/portal/workflow/$key/my-info").withPlayer(other), Map::class.java)
        assertEquals(0, (hostInfo["units"] as List<*>).size)
        assertEquals(1, (otherInfo["units"] as List<*>).size)
    }

    @Test
    fun `logout revokes the token`() {
        val token = createPlayer("bob")
        client.toBlocking().exchange(HttpRequest.POST("/api/portal/player/logout", "").withPlayer(token), Any::class.java)
        assertStatus(HttpStatus.UNAUTHORIZED) {
            client.toBlocking().exchange(HttpRequest.GET<Any>("/api/portal/player/me").withPlayer(token), Map::class.java)
        }
    }

    @Test
    fun `removed anonymous endpoints are gone`() {
        listOf(
            HttpRequest.GET<Any>("/api/portal/player"),
            HttpRequest.GET<Any>("/api/portal/game"),
            HttpRequest.DELETE<Any>("/api/portal/game/1"),
            HttpRequest.DELETE<Any>("/api/portal/player/1"),
        ).forEach { request ->
            val ex = assertThrows(HttpClientResponseException::class.java) { client.toBlocking().exchange(request, Any::class.java) }
            assertTrue(ex.status == HttpStatus.UNAUTHORIZED || ex.status == HttpStatus.NOT_FOUND || ex.status == HttpStatus.METHOD_NOT_ALLOWED, "${request.path} -> ${ex.status}")
        }
    }

    private fun createPlayer(name: String): String {
        val response = client.toBlocking().exchange(HttpRequest.POST("/api/portal/player", mapOf("name" to name)), Map::class.java)
        return response.headers.getAll("Set-Cookie").first { it.startsWith(PortalPlayerCookie.NAME + "=") }
            .substringAfter("=").substringBefore(";")
    }

    private fun <T : Any> MutableHttpRequest<T>.withPlayer(token: String): MutableHttpRequest<T> =
        cookie(io.micronaut.http.cookie.Cookie.of(PortalPlayerCookie.NAME, token))

    private fun assertStatus(expected: HttpStatus, context: String = "", block: () -> Unit) {
        val ex = assertThrows(HttpClientResponseException::class.java) { block() }
        assertEquals(expected, ex.status, context)
    }
}
