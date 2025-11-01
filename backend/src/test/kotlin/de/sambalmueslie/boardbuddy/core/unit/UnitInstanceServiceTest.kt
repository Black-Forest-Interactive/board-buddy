package de.sambalmueslie.boardbuddy.core.unit

import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.event.api.EventConsumer
import de.sambalmueslie.boardbuddy.core.unit.api.*
import io.micronaut.data.model.Pageable
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.mockk.*
import jakarta.inject.Inject
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.testcontainers.junit.jupiter.Testcontainers

@MicronautTest
@Testcontainers
class UnitInstanceServiceTest {

    @Inject
    lateinit var service: UnitInstanceService

    @Inject
    var typeService: UnitTypeService? = null
        set(value) {
            field = value
            type = typeService!!.create(UnitTypeChangeRequest("name", UnitClass.INFANTRY, UnitClass.CAVALRY, PointsRange(1, 3), PointsRange(1, 3), 4))
            request = UnitInstanceChangeRequest(type, 3, 1, 1)
        }

    @Inject
    lateinit var eventService: EventService

    private val eventCollector: EventConsumer<UnitInstance> = mockk()
    private lateinit var type: UnitType
    private lateinit var request: UnitInstanceChangeRequest

    init {
        every { eventCollector.created(any()) } just Runs
        every { eventCollector.updated(any()) } just Runs
        every { eventCollector.deleted(any()) } just Runs
    }

    @BeforeEach
    fun setup() {
        eventService.register(UnitInstance::class, eventCollector)
    }

    @AfterEach
    fun teardown() {
        eventService.unregister(UnitInstance::class, eventCollector)
        service.deleteAll()
    }

    @Test
    fun testCrudOperations() {
        // CREATE
        val response = service.create(request)
        var reference = UnitInstance(response.id, type, request.damage, request.health, request.level)
        assertEquals(reference, response)
        verify { eventCollector.created(reference) }

        // GETTER
        assertEquals(reference, service.get(reference.id))
        assertEquals(listOf(reference), service.getAll(Pageable.from(0)).content)

        // UPDATE
        val update = UnitInstanceChangeRequest(type, 3, 2, 2)
        reference = UnitInstance(response.id, type, update.damage, update.health, update.level)
        assertEquals(reference, service.update(reference.id, update))
        verify { eventCollector.updated(reference) }

        // DELETE
        service.delete(reference.id)
        verify { eventCollector.deleted(reference) }
        verify { eventCollector.hashCode() }
        confirmVerified(eventCollector)

        // EMPTY GETTER
        assertNull(service.get(0))
        assertEquals(emptyList<UnitInstance>(), service.getAll(Pageable.from(0)).content)
    }

    @Test
    fun testUpdateWithInvalidId() {
        service.create(request)

        val updateResponse = service.update(99, request)
        val updateReference = UnitInstance(updateResponse.id, type, request.damage, request.health, request.level)
        assertEquals(updateReference, updateResponse)
        verify { eventCollector.created(updateReference) }
    }

    @Test
    fun testValidation() {
        val invalidLevelRequest = UnitInstanceChangeRequest(type, request.damage, request.health, 0)
        assertThrows<UnitInstanceLevelValidationFailed> { service.create(invalidLevelRequest) }

        val invalidDamageRequest1 = UnitInstanceChangeRequest(type, -1, request.health, request.level)
        assertThrows<UnitInstanceDamageValidationFailed> { service.create(invalidDamageRequest1) }

        val invalidDamageRequest2 = UnitInstanceChangeRequest(type, type.damagePoints.max + 1, request.health, request.level)
        assertThrows<UnitInstanceDamageValidationFailed> { service.create(invalidDamageRequest2) }

        val invalidHealthRequest1 = UnitInstanceChangeRequest(type, request.damage, -1, request.level)
        assertThrows<UnitInstanceHealthValidationFailed> { service.create(invalidHealthRequest1) }

        val invalidHealthRequest2 = UnitInstanceChangeRequest(type, request.damage, type.healthPoints.max + 1, request.level)
        assertThrows<UnitInstanceHealthValidationFailed> { service.create(invalidHealthRequest2) }

        val response = service.create(request)
        assertThrows<UnitInstanceLevelValidationFailed> { service.update(response.id, invalidLevelRequest) }
        assertThrows<UnitInstanceDamageValidationFailed> { service.update(response.id, invalidDamageRequest1) }
        assertThrows<UnitInstanceDamageValidationFailed> { service.update(response.id, invalidDamageRequest2) }
        assertThrows<UnitInstanceHealthValidationFailed> { service.update(response.id, invalidHealthRequest1) }
        assertThrows<UnitInstanceHealthValidationFailed> { service.update(response.id, invalidHealthRequest2) }
    }

    @Test
    fun deleteDependencies() {
        val response = service.create(request)
        val reference = UnitInstance(response.id, type, request.damage, request.health, request.level)
        // GETTER
        assertEquals(reference, service.get(reference.id))

        typeService!!.delete(type.id)
        verify { eventCollector.deleted(reference) }
        assertNull(service.get(0))
    }
}