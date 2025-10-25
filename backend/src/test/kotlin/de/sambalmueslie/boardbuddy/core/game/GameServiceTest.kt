package de.sambalmueslie.boardbuddy.core.game

import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.event.api.EventConsumer
import de.sambalmueslie.boardbuddy.core.game.api.DescriptionValidationFailed
import de.sambalmueslie.boardbuddy.core.game.api.Game
import de.sambalmueslie.boardbuddy.core.game.api.GameChangeRequest
import de.sambalmueslie.boardbuddy.core.game.api.NameValidationFailed
import io.micronaut.data.model.Pageable
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.mockk.*
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import org.junit.jupiter.api.assertThrows

@MicronautTest
class GameServiceTest {

    @Inject
    lateinit var service: GameService

    @Inject
    lateinit var eventService: EventService

    @Test
    fun testCrudOperations() {
        // SETUP EVENT COLLECTOR
        val eventCollector: EventConsumer<Game> = mockk()
        every { eventCollector.created(any()) } just Runs
        every { eventCollector.updated(any()) } just Runs
        every { eventCollector.deleted(any()) } just Runs
        eventService.register(Game::class, eventCollector)

        // EMPTY GETTER
        assertNull(service.get(0))
        assertEquals(emptyList<Game>(), service.getAll(Pageable.from(0)).content)

        // CREATE
        val request = GameChangeRequest("name", "description")
        var reference = Game(1, request.name, request.description)
        assertEquals(reference, service.create(request))
        verify { eventCollector.created(reference) }

        assertThrows<NameValidationFailed> { service.create(GameChangeRequest("", request.description)) }
        assertThrows<DescriptionValidationFailed> { service.create(GameChangeRequest(request.name, "")) }

        // GETTER
        assertEquals(reference, service.get(reference.id))
        assertEquals(listOf(reference), service.getAll(Pageable.from(0)).content)

        // UPDATE
        val update = GameChangeRequest("name-update", "description-update")
        reference = Game(1, update.name, update.description)
        assertEquals(reference, service.update(reference.id, update))
        verify { eventCollector.updated(reference) }

        assertThrows<NameValidationFailed> { service.update(reference.id, (GameChangeRequest("", request.description))) }
        assertThrows<DescriptionValidationFailed> { service.update(reference.id, (GameChangeRequest(request.name, ""))) }

        val secondReference = Game(2, request.name, request.description)
        assertEquals(secondReference, service.update(99, request))
        verify { eventCollector.created(secondReference) }

        // DELETE
        service.delete(reference.id)
        verify { eventCollector.deleted(reference) }
        service.delete(secondReference.id)
        verify { eventCollector.deleted(secondReference) }
        verify { eventCollector.hashCode() }
        confirmVerified(eventCollector)

        // EMPTY GETTER
        assertNull(service.get(0))
        assertEquals(emptyList<Game>(), service.getAll(Pageable.from(0)).content)

    }

}