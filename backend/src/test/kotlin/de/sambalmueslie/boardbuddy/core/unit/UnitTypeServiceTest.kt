package de.sambalmueslie.boardbuddy.core.unit

import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.event.api.EventConsumer
import de.sambalmueslie.boardbuddy.core.unit.api.*
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
class UnitTypeServiceTest {
    @Inject
    lateinit var service: UnitTypeService

    @Inject
    lateinit var eventService: EventService

    @Test
    fun testCrudOperations() {
        // SETUP EVENT COLLECTOR
        val eventCollector: EventConsumer<UnitType> = mockk()
        every { eventCollector.created(any()) } just Runs
        every { eventCollector.updated(any()) } just Runs
        every { eventCollector.deleted(any()) } just Runs
        eventService.register(UnitType::class, eventCollector)

        // EMPTY GETTER
        assertNull(service.get(0))
        assertEquals(emptyList<UnitType>(), service.getAll(Pageable.from(0)).content)

        // CREATE
        val request = UnitTypeChangeRequest("name", UnitClass.INFANTRY, UnitClass.CAVALRY, PointsRange(1, 3), PointsRange(1, 3), 4)
        var reference = UnitType(1, request.name, request.unitClass, request.counterClass, request.damagePoints, request.healthPoints, request.maxLevel)
        assertEquals(reference, service.create(request))
        verify { eventCollector.created(reference) }

        assertThrows<UnitTypeNameValidationFailed> { service.create(UnitTypeChangeRequest("", request.unitClass, request.counterClass, request.damagePoints, request.healthPoints, request.maxLevel)) }
        assertThrows<UnitTypeCounterClassValidationFailed> {
            service.create(
                UnitTypeChangeRequest(
                    request.name,
                    request.unitClass,
                    request.unitClass,
                    request.damagePoints,
                    request.healthPoints,
                    request.maxLevel
                )
            )
        }
        assertThrows<UnitTypeDamageValidationFailed> {
            service.create(
                UnitTypeChangeRequest(
                    request.name,
                    request.unitClass,
                    request.counterClass,
                    PointsRange(-1, 3),
                    request.healthPoints,
                    request.maxLevel
                )
            )
        }
        assertThrows<UnitTypeDamageValidationFailed> {
            service.create(
                UnitTypeChangeRequest(
                    request.name,
                    request.unitClass,
                    request.counterClass,
                    PointsRange(3, 1),
                    request.healthPoints,
                    request.maxLevel
                )
            )
        }
        assertThrows<UnitTypeHealthValidationFailed> {
            service.create(
                UnitTypeChangeRequest(
                    request.name,
                    request.unitClass,
                    request.counterClass,
                    request.damagePoints,
                    PointsRange(-1, 3),
                    request.maxLevel
                )
            )
        }
        assertThrows<UnitTypeHealthValidationFailed> {
            service.create(
                UnitTypeChangeRequest(
                    request.name,
                    request.unitClass,
                    request.counterClass,
                    request.damagePoints,
                    PointsRange(2, 1),
                    request.maxLevel
                )
            )
        }
        assertThrows<UnitTypeLevelValidationFailed> { service.create(UnitTypeChangeRequest(request.name, request.unitClass, request.counterClass, request.damagePoints, request.healthPoints, 0)) }

        // GETTER
        assertEquals(reference, service.get(reference.id))
        assertEquals(listOf(reference), service.getAll(Pageable.from(0)).content)

        // UPDATE
        val update = UnitTypeChangeRequest("name-update", UnitClass.CAVALRY, UnitClass.ARTILLERY, PointsRange(2, 4), PointsRange(2, 4), 4)
        reference = UnitType(1, update.name, update.unitClass, update.counterClass, update.damagePoints, update.healthPoints, update.maxLevel)
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
}