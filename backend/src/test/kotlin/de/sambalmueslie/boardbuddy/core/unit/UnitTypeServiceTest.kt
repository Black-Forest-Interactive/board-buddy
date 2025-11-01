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
class UnitTypeServiceTest {
    @Inject
    lateinit var service: UnitTypeService

    @Inject
    lateinit var eventService: EventService

    private val eventCollector: EventConsumer<UnitType> = mockk()
    private val request = UnitTypeChangeRequest("name", UnitClass.INFANTRY, UnitClass.CAVALRY, PointsRange(1, 3), PointsRange(1, 3), 4)

    init {
        every { eventCollector.created(any()) } just Runs
        every { eventCollector.updated(any()) } just Runs
        every { eventCollector.deleted(any()) } just Runs
    }

    @BeforeEach
    fun setup() {
        eventService.register(UnitType::class, eventCollector)
    }

    @AfterEach
    fun teardown() {
        eventService.unregister(UnitType::class, eventCollector)
        service.deleteAll()
    }


    @Test
    fun testCrudOperations() {
        // CREATE
        val response = service.create(request)
        var reference = UnitType(response.id, request.name, request.unitClass, request.counterClass, request.damagePoints, request.healthPoints, request.maxLevel)
        assertEquals(reference, response)
        verify { eventCollector.created(reference) }

        // GETTER
        assertEquals(reference, service.get(reference.id))
        assertEquals(listOf(reference), service.getAll(Pageable.from(0)).content)

        // UPDATE
        val update = UnitTypeChangeRequest("name-update", UnitClass.CAVALRY, UnitClass.ARTILLERY, PointsRange(2, 4), PointsRange(2, 4), 4)
        reference = UnitType(response.id, update.name, update.unitClass, update.counterClass, update.damagePoints, update.healthPoints, update.maxLevel)
        assertEquals(reference, service.update(reference.id, update))
        verify { eventCollector.updated(reference) }

        // DELETE
        service.delete(reference.id)
        verify { eventCollector.deleted(reference) }
        verify { eventCollector.hashCode() }
        confirmVerified(eventCollector)

        // EMPTY GETTER
        assertNull(service.get(0))
        assertEquals(emptyList<UnitType>(), service.getAll(Pageable.from(0)).content)

    }


    @Test
    fun testUpdateWithInvalidId() {
        service.create(request)

        val updateResponse = service.update(99, request)
        val updateReference = UnitType(updateResponse.id, request.name, request.unitClass, request.counterClass, request.damagePoints, request.healthPoints, request.maxLevel)
        assertEquals(updateReference, updateResponse)
        verify { eventCollector.created(updateReference) }
    }

    @Test
    fun testValidation() {

        val invalidNameRequest = UnitTypeChangeRequest("", request.unitClass, request.counterClass, request.damagePoints, request.healthPoints, request.maxLevel)
        assertThrows<UnitTypeNameValidationFailed> { service.create(invalidNameRequest) }
        val invalidCounterClassRequest = UnitTypeChangeRequest(request.name, request.unitClass, request.unitClass, request.damagePoints, request.healthPoints, request.maxLevel)
        assertThrows<UnitTypeCounterClassValidationFailed> { service.create(invalidCounterClassRequest) }

        val invalidDamageRequest1 = UnitTypeChangeRequest(request.name, request.unitClass, request.counterClass, PointsRange(-1, 3), request.healthPoints, request.maxLevel)
        assertThrows<UnitTypeDamageValidationFailed> { service.create(invalidDamageRequest1) }

        val invalidDamageRequest2 = UnitTypeChangeRequest(request.name, request.unitClass, request.counterClass, PointsRange(3, 1), request.healthPoints, request.maxLevel)
        assertThrows<UnitTypeDamageValidationFailed> { service.create(invalidDamageRequest2) }

        val invalidHealthRequest1 = UnitTypeChangeRequest(request.name, request.unitClass, request.counterClass, request.damagePoints, PointsRange(-1, 3), request.maxLevel)
        assertThrows<UnitTypeHealthValidationFailed> { service.create(invalidHealthRequest1) }

        val invalidHealthRequest2 = UnitTypeChangeRequest(request.name, request.unitClass, request.counterClass, request.damagePoints, PointsRange(2, 1), request.maxLevel)
        assertThrows<UnitTypeHealthValidationFailed> { service.create(invalidHealthRequest2) }

        val invalidUnitLevelRequest = UnitTypeChangeRequest(request.name, request.unitClass, request.counterClass, request.damagePoints, request.healthPoints, 0)
        assertThrows<UnitTypeLevelValidationFailed> { service.create(invalidUnitLevelRequest) }


        val response = service.create(request)
        assertThrows<UnitTypeNameValidationFailed> { service.update(response.id, invalidNameRequest) }
        assertThrows<UnitTypeCounterClassValidationFailed> { service.update(response.id, invalidCounterClassRequest) }
        assertThrows<UnitTypeDamageValidationFailed> { service.update(response.id, invalidDamageRequest1) }
        assertThrows<UnitTypeDamageValidationFailed> { service.update(response.id, invalidDamageRequest2) }
        assertThrows<UnitTypeHealthValidationFailed> { service.update(response.id, invalidHealthRequest1) }
        assertThrows<UnitTypeHealthValidationFailed> { service.update(response.id, invalidHealthRequest2) }
        assertThrows<UnitTypeLevelValidationFailed> { service.update(response.id, invalidUnitLevelRequest) }
        service.delete(response.id)
    }

}