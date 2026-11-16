package de.sambalmueslie.boardbuddy.gateway.admin

import de.sambalmueslie.boardbuddy.gateway.admin.security.AdminRole
import io.micronaut.context.annotation.Property
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.security.token.jwt.generator.JwtTokenGenerator
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.time.Instant

@MicronautTest
@Property(name = "micronaut.security.enabled", value = "true")
@Property(name = "micronaut.security.oauth2.enabled", value = "false")
// local HS256 key instead of the keycloak JWKS, the remote JWKS is pointed to an unused port
@Property(name = "micronaut.security.token.jwt.signatures.secret.generator.secret", value = "test-secret-that-is-at-least-256-bits-long-0123456789")
@Property(name = "micronaut.security.token.jwt.signatures.jwks.keycloak.url", value = "http://localhost:1/certs")
@Property(name = "micronaut.security.token.jwt.claims-validators.issuer", value = ISSUER)
@Property(name = "board-buddy.admin.authorized-party", value = CLIENT)
class AdminSecurityTest {

    @Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Inject
    lateinit var generator: JwtTokenGenerator

    private val adminRoutes = listOf("game", "player", "session", "nation", "rule-set", "technology", "unit-type")

    @Test
    fun `admin role grants access`() {
        val token = token(roles = listOf(AdminRole.ADMIN))
        adminRoutes.forEach { route ->
            val response = client.toBlocking().exchange(HttpRequest.GET<Any>("/api/admin/$route").bearerAuth(token), Any::class.java)
            assertEquals(HttpStatus.OK, response.status, route)
        }
    }

    @Test
    fun `authenticated user without admin role is forbidden`() {
        val token = token(roles = listOf("offline_access"))
        adminRoutes.forEach { route -> assertStatus(HttpStatus.FORBIDDEN, route) { get("/api/admin/$route", token) } }
    }

    @Test
    fun `tokens from a wrong issuer or client are rejected`() {
        assertStatus(HttpStatus.UNAUTHORIZED) { get("/api/admin/game", token(roles = listOf(AdminRole.ADMIN), issuer = "https://evil.example/realms/x")) }
        assertStatus(HttpStatus.UNAUTHORIZED) { get("/api/admin/game", token(roles = listOf(AdminRole.ADMIN), azp = "some-other-client")) }
        assertStatus(HttpStatus.UNAUTHORIZED) { get("/api/admin/game", null) }
    }

    @Test
    fun `admin endpoints are no longer served below the portal path`() {
        val token = token(roles = listOf(AdminRole.ADMIN))
        listOf("nation", "rule-set", "technology", "unit-type").forEach { route ->
            val ex = assertThrows(HttpClientResponseException::class.java) { get("/api/portal/$route", token) }
            assertEquals(true, ex.status == HttpStatus.NOT_FOUND || ex.status == HttpStatus.UNAUTHORIZED || ex.status == HttpStatus.FORBIDDEN, "$route -> ${ex.status}")
        }
    }

    private fun get(path: String, token: String?) {
        val request = HttpRequest.GET<Any>(path)
        if (token != null) request.bearerAuth(token)
        client.toBlocking().exchange(request, Any::class.java)
    }

    private fun token(roles: List<String>, issuer: String = ISSUER, azp: String = CLIENT): String {
        val now = Instant.now().epochSecond
        val claims = mapOf<String, Any>(
            "sub" to "admin-user",
            "iss" to issuer,
            "azp" to azp,
            "iat" to now,
            "exp" to now + 300,
            "realm_access" to mapOf("roles" to roles),
        )
        return generator.generateToken(claims).orElseThrow()
    }

    private fun assertStatus(expected: HttpStatus, context: String = "", block: () -> Unit) {
        val ex = assertThrows(HttpClientResponseException::class.java) { block() }
        assertEquals(expected, ex.status, context)
    }
}

private const val ISSUER = "https://auth.test/realms/board-buddy"
private const val CLIENT = "board-buddy-admin"
