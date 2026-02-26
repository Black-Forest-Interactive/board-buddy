package de.sambalmueslie.boardbuddy.core.unit

import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.event.api.EventConsumer
import de.sambalmueslie.boardbuddy.core.unit.api.*
import de.sambalmueslie.boardbuddy.engine.api.UnitType
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

    private val eventCollector: EventConsumer<UnitDefinition> = mockk()
    private val request = UnitDefinitionChangeRequest("name", UnitType.INFANTRY, UnitType.CAVALRY, PointsRange(1, 3), PointsRange(1, 3), 4)

    init {
        every { eventCollector.created(any()) } just Runs
        every { eventCollector.updated(any()) } just Runs
        every { eventCollector.deleted(any()) } just Runs
    }

    @BeforeEach
    fun setup() {
        eventService.register(UnitDefinition::class, eventCollector)
    }

    @AfterEach
    fun teardown() {
        eventService.unregister(UnitDefinition::class, eventCollector)
        service.deleteAll()
    }


    @Test
    fun testCrudOperations() {
        // CREATE
        val response = service.create(request)
        var reference = UnitDefinition(response.id, request.name, request.unitType, request.counterClass, request.damagePoints, request.healthPoints, request.maxLevel)
        assertEquals(reference, response)
        verify { eventCollector.created(reference) }

        // GETTER
        assertEquals(reference, service.get(reference.id))
        assertEquals(listOf(reference), service.getAll(Pageable.from(0)).content)

        // UPDATE
        val update = UnitDefinitionChangeRequest("name-update", UnitType.CAVALRY, UnitType.ARTILLERY, PointsRange(2, 4), PointsRange(2, 4), 4)
        reference = UnitDefinition(response.id, update.name, update.unitType, update.counterClass, update.damagePoints, update.healthPoints, update.maxLevel)
        assertEquals(reference, service.update(reference.id, update))
        verify { eventCollector.updated(reference) }

        // DELETE
        service.delete(reference.id)
        verify { eventCollector.deleted(reference) }
        verify { eventCollector.hashCode() }
        confirmVerified(eventCollector)

        // EMPTY GETTER
        assertNull(service.get(0))
        assertEquals(emptyList<UnitDefinition>(), service.getAll(Pageable.from(0)).content)

    }


    @Test
    fun testUpdateWithInvalidId() {
        service.create(request)

        val updateResponse = service.update(99, request)
        val updateReference = UnitDefinition(updateResponse.id, request.name, request.unitType, request.counterClass, request.damagePoints, request.healthPoints, request.maxLevel)
        assertEquals(updateReference, updateResponse)
        verify { eventCollector.created(updateReference) }
    }

    @Test
    fun testValidation() {

        val invalidNameRequest = UnitDefinitionChangeRequest("", request.unitType, request.counterClass, request.damagePoints, request.healthPoints, request.maxLevel)
        assertThrows<UnitDefinitionNameValidationFailed> { service.create(invalidNameRequest) }
        val invalidCounterClassRequest = UnitDefinitionChangeRequest(request.name, request.unitType, request.unitType, request.damagePoints, request.healthPoints, request.maxLevel)
        assertThrows<UnitDefinitionCounterClassValidationFailed> { service.create(invalidCounterClassRequest) }

        val invalidDamageRequest1 = UnitDefinitionChangeRequest(request.name, request.unitType, request.counterClass, PointsRange(-1, 3), request.healthPoints, request.maxLevel)
        assertThrows<UnitDefinitionDamageValidationFailed> { service.create(invalidDamageRequest1) }

        val invalidDamageRequest2 = UnitDefinitionChangeRequest(request.name, request.unitType, request.counterClass, PointsRange(3, 1), request.healthPoints, request.maxLevel)
        assertThrows<UnitDefinitionDamageValidationFailed> { service.create(invalidDamageRequest2) }

        val invalidHealthRequest1 = UnitDefinitionChangeRequest(request.name, request.unitType, request.counterClass, request.damagePoints, PointsRange(-1, 3), request.maxLevel)
        assertThrows<UnitDefinitionHealthValidationFailed> { service.create(invalidHealthRequest1) }

        val invalidHealthRequest2 = UnitDefinitionChangeRequest(request.name, request.unitType, request.counterClass, request.damagePoints, PointsRange(2, 1), request.maxLevel)
        assertThrows<UnitDefinitionHealthValidationFailed> { service.create(invalidHealthRequest2) }

        val invalidUnitLevelRequest = UnitDefinitionChangeRequest(request.name, request.unitType, request.counterClass, request.damagePoints, request.healthPoints, 0)
        assertThrows<UnitDefinitionLevelValidationFailed> { service.create(invalidUnitLevelRequest) }


        val response = service.create(request)
        assertThrows<UnitDefinitionNameValidationFailed> { service.update(response.id, invalidNameRequest) }
        assertThrows<UnitDefinitionCounterClassValidationFailed> { service.update(response.id, invalidCounterClassRequest) }
        assertThrows<UnitDefinitionDamageValidationFailed> { service.update(response.id, invalidDamageRequest1) }
        assertThrows<UnitDefinitionDamageValidationFailed> { service.update(response.id, invalidDamageRequest2) }
        assertThrows<UnitDefinitionHealthValidationFailed> { service.update(response.id, invalidHealthRequest1) }
        assertThrows<UnitDefinitionHealthValidationFailed> { service.update(response.id, invalidHealthRequest2) }
        assertThrows<UnitDefinitionLevelValidationFailed> { service.update(response.id, invalidUnitLevelRequest) }
        service.delete(response.id)
    }

}