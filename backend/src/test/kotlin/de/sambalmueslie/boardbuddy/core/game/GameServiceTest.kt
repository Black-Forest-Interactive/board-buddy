package de.sambalmueslie.boardbuddy.core.game

import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.event.api.EventConsumer
import de.sambalmueslie.boardbuddy.core.game.api.Game
import de.sambalmueslie.boardbuddy.core.game.api.GameChangeRequest
import de.sambalmueslie.boardbuddy.core.game.api.GameDescriptionValidationFailed
import de.sambalmueslie.boardbuddy.core.game.api.GameNameValidationFailed
import de.sambalmueslie.boardbuddy.core.ruleset.RuleSetService
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSet
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSetChangeRequest
import io.micronaut.data.model.Pageable
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.mockk.*
import jakarta.inject.Inject
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.testcontainers.junit.jupiter.Testcontainers

@MicronautTest()
@Testcontainers
class GameServiceTest {

    @Inject
    lateinit var service: GameService

    @Inject
    lateinit var ruleSetService: RuleSetService

    @Inject
    lateinit var eventService: EventService


    private val eventCollector: EventConsumer<Game> = mockk()
    private val request = GameChangeRequest("name", "description")

    init {
        every { eventCollector.created(any()) } just Runs
        every { eventCollector.updated(any()) } just Runs
        every { eventCollector.deleted(any()) } just Runs
    }

    @BeforeEach
    fun setup() {
        eventService.register(Game::class, eventCollector)
    }

    @AfterEach
    fun teardown() {
        eventService.unregister(Game::class, eventCollector)
        service.deleteAll()
        ruleSetService.deleteAll()
    }


    @Test
    fun testCrudOperations() {

        // CREATE
        val response = service.create(request)
        var reference = Game(response.id, request.name, request.description, emptyList())
        assertEquals(reference, response)
        verify { eventCollector.created(reference) }

        // GETTER
        assertEquals(reference, service.get(reference.id))
        assertEquals(listOf(reference), service.getAll(Pageable.from(0)).content)

        // UPDATE
        val update = GameChangeRequest("name-update", "description-update")
        reference = Game(response.id, update.name, update.description, emptyList())
        assertEquals(reference, service.update(reference.id, update))
        verify { eventCollector.updated(reference) }

        // DELETE
        service.delete(reference.id)
        verify { eventCollector.deleted(reference) }
        verify { eventCollector.hashCode() }
        confirmVerified(eventCollector)

        // EMPTY GETTER
        assertNull(service.get(0))
        assertEquals(emptyList<Game>(), service.getAll(Pageable.from(0)).content)
    }

    @Test
    fun testUpdateWithInvalidId() {
        service.create(request)

        val updateResponse = service.update(99, request)
        val updateReference = Game(updateResponse.id, request.name, request.description, emptyList())
        assertEquals(updateReference, updateResponse)
        verify { eventCollector.created(updateReference) }
    }

    @Test
    fun testValidation() {
        assertThrows<GameNameValidationFailed> { service.create(GameChangeRequest("", request.description)) }
        assertThrows<GameDescriptionValidationFailed> { service.create(GameChangeRequest(request.name, "")) }

        val response = service.create(request)
        assertThrows<GameNameValidationFailed> { service.update(response.id, (GameChangeRequest("", request.description))) }
        assertThrows<GameDescriptionValidationFailed> { service.update(response.id, (GameChangeRequest(request.name, ""))) }
        service.delete(response.id)
    }

    @Test
    fun testRuleSetRelations() {
        val game = service.create(request)
        val ruleSet = ruleSetService.create(RuleSetChangeRequest("name"))

        val assigned = service.assignRuleSet(game, ruleSet)
        assertNotNull(assigned)
        assertEquals(listOf(ruleSet), assigned!!.ruleSets)
        verify { eventCollector.updated(assigned) }

        val response = service.get(assigned.id)
        assertNotNull(response)
        assertEquals(assigned, response)

        val revoked = service.revokeRuleSet(game, ruleSet)
        assertNotNull(revoked)
        assertEquals(listOf<RuleSet>(), revoked!!.ruleSets)
        verify { eventCollector.updated(revoked) }
    }

    @Test
    fun testDeletionWithRelations() {
        val game = service.create(request)
        val ruleSet = ruleSetService.create(RuleSetChangeRequest("name"))

        service.assignRuleSet(game, ruleSet)

        val response = service.get(game.id)
        assertNotNull(response)

        service.delete(response!!.id)
        verify { eventCollector.deleted(response) }
    }
}