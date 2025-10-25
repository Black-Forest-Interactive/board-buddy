package de.sambalmueslie.boardbuddy.core.player

import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.event.api.EventConsumer
import de.sambalmueslie.boardbuddy.core.player.api.NameValidationFailed
import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.core.player.api.PlayerChangeRequest
import io.micronaut.data.model.Pageable
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.mockk.*
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import org.junit.jupiter.api.assertThrows
import org.testcontainers.junit.jupiter.Testcontainers

@MicronautTest
@Testcontainers
class PlayerServiceTest {
    @Inject
    lateinit var service: PlayerService

    @Inject
    lateinit var eventService: EventService

    @Test
    fun testCrudOperations() {
        // SETUP EVENT COLLECTOR
        val eventCollector: EventConsumer<Player> = mockk()
        every { eventCollector.created(any()) } just Runs
        every { eventCollector.updated(any()) } just Runs
        every { eventCollector.deleted(any()) } just Runs
        eventService.register(Player::class, eventCollector)

        // EMPTY GETTER
        assertNull(service.get(0))
        assertEquals(emptyList<Player>(), service.getAll(Pageable.from(0)).content)

        // CREATE
        val request = PlayerChangeRequest("name")
        var reference = Player(1, request.name)
        assertEquals(reference, service.create(request))
        verify { eventCollector.created(reference) }

        assertThrows<NameValidationFailed> { service.create(PlayerChangeRequest("")) }

        // GETTER
        assertEquals(reference, service.get(reference.id))
        assertEquals(listOf(reference), service.getAll(Pageable.from(0)).content)

        // UPDATE
        val update = PlayerChangeRequest("name-update")
        reference = Player(1, update.name)
        assertEquals(reference, service.update(reference.id, update))
        verify { eventCollector.updated(reference) }

        assertThrows<NameValidationFailed> { service.update(reference.id, (PlayerChangeRequest(""))) }

        val secondReference = Player(2, request.name)
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
        assertEquals(emptyList<Player>(), service.getAll(Pageable.from(0)).content)

    }
}