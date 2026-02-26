package de.sambalmueslie.boardbuddy.core.session

import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.event.api.EventConsumer
import de.sambalmueslie.boardbuddy.core.game.GameService
import de.sambalmueslie.boardbuddy.core.game.api.GameChangeRequest
import de.sambalmueslie.boardbuddy.core.player.PlayerService
import de.sambalmueslie.boardbuddy.core.player.api.PlayerChangeRequest
import de.sambalmueslie.boardbuddy.core.ruleset.RuleSetService
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSetChangeRequest
import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.core.session.api.GameSessionChangeRequest
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import jakarta.inject.Inject
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Testcontainers

@MicronautTest()
@Testcontainers
class GameSessionServiceTest {
    @Inject
    lateinit var service: GameSessionService

    @Inject
    lateinit var gameService: GameService

    @Inject
    lateinit var playerService: PlayerService

    @Inject
    lateinit var ruleSetService: RuleSetService

    @Inject
    lateinit var eventService: EventService

    private val eventCollector: EventConsumer<GameSession> = mockk()

    init {
        every { eventCollector.created(any()) } just Runs
        every { eventCollector.updated(any()) } just Runs
        every { eventCollector.deleted(any()) } just Runs
    }

    @BeforeEach
    fun setup() {
        eventService.register(GameSession::class, eventCollector)
    }

    @AfterEach
    fun teardown() {
        eventService.unregister(GameSession::class, eventCollector)
        service.deleteAll()
        gameService.deleteAll()
        playerService.deleteAll()
        ruleSetService.deleteAll()
    }

    @Test
    fun testCrudOperations() {
        var game = gameService.create(GameChangeRequest("game", "description"))
        val ruleSet = ruleSetService.create(RuleSetChangeRequest("rule-set"))
        game = gameService.assignRuleSet(game, ruleSet)!!

        val host = playerService.create(PlayerChangeRequest("host"))
        val player = playerService.create(PlayerChangeRequest("player"))

        val request = GameSessionChangeRequest("session", host, game, ruleSet)
        var response = service.create(request)
        var reference = GameSession(response.id, response.key, request.name, request.host, emptyList(), request.game, request.ruleSet, response.timestamp)
        assertEquals(reference, response)

        response = service.assignPlayer(response, player)!!
        reference = GameSession(response.id, response.key, request.name, request.host, listOf(player), request.game, request.ruleSet, response.timestamp)
        assertEquals(reference, response)

        response = service.revokePlayer(response, player)!!
        reference = GameSession(response.id, response.key, request.name, request.host, emptyList(), request.game, request.ruleSet, response.timestamp)
        assertEquals(reference, response)
    }
}