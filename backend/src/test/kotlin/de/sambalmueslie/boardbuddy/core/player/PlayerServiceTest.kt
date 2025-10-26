package de.sambalmueslie.boardbuddy.core.player

import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.event.api.EventConsumer
import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.core.player.api.PlayerChangeRequest
import de.sambalmueslie.boardbuddy.core.player.api.PlayerNameValidationFailed
import io.micronaut.data.model.Pageable
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.mockk.*
import jakarta.inject.Inject
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.testcontainers.junit.jupiter.Testcontainers

@MicronautTest
@Testcontainers
class PlayerServiceTest {
    @Inject
    lateinit var service: PlayerService

    @Inject
    lateinit var eventService: EventService


    private val eventCollector: EventConsumer<Player> = mockk()
    private val request = PlayerChangeRequest("name")

    init {
        every { eventCollector.created(any()) } just Runs
        every { eventCollector.updated(any()) } just Runs
        every { eventCollector.deleted(any()) } just Runs
    }

    @BeforeEach
    fun setup() {
        eventService.register(Player::class, eventCollector)
    }

    @AfterEach
    fun teardown() {
        eventService.unregister(Player::class, eventCollector)
        service.deleteAll()
    }


    @Test
    fun testCrudOperations() {
        // CREATE
        val response = service.create(request)
        var reference = Player(response.id, request.name)
        assertEquals(reference, response)
        verify { eventCollector.created(reference) }

        // GETTER
        assertEquals(reference, service.get(reference.id))
        assertEquals(listOf(reference), service.getAll(Pageable.from(0)).content)

        // UPDATE
        val update = PlayerChangeRequest("name-update")
        reference = Player(response.id, update.name)
        assertEquals(reference, service.update(reference.id, update))
        verify { eventCollector.updated(reference) }

        // DELETE
        service.delete(reference.id)
        verify { eventCollector.deleted(reference) }
        verify { eventCollector.hashCode() }
        confirmVerified(eventCollector)

        // EMPTY GETTER
        assertNull(service.get(0))
        assertEquals(emptyList<Player>(), service.getAll(Pageable.from(0)).content)
    }


    @Test
    fun testUpdateWithInvalidId() {
        service.create(request)

        val updateResponse = service.update(99, request)
        val updateReference = Player(updateResponse.id, request.name)
        assertEquals(updateReference, updateResponse)
        verify { eventCollector.created(updateReference) }
    }

    @Test
    fun testValidation() {
        assertThrows<PlayerNameValidationFailed> { service.create(PlayerChangeRequest("")) }

        val response = service.create(request)
        assertThrows<PlayerNameValidationFailed> { service.update(response.id, PlayerChangeRequest("")) }
        service.delete(response.id)
    }

}