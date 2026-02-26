package de.sambalmueslie.boardbuddy.core.ruleset

import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.event.api.EventConsumer
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSet
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSetChangeRequest
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSetNameValidationFailed
import de.sambalmueslie.boardbuddy.core.unit.UnitTypeService
import de.sambalmueslie.boardbuddy.core.unit.api.PointsRange
import de.sambalmueslie.boardbuddy.core.unit.api.UnitDefinition
import de.sambalmueslie.boardbuddy.core.unit.api.UnitDefinitionChangeRequest
import de.sambalmueslie.boardbuddy.engine.api.UnitType
import io.micronaut.data.model.Pageable
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.mockk.*
import jakarta.inject.Inject
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.testcontainers.junit.jupiter.Testcontainers

@MicronautTest()
@Testcontainers
class RuleSetServiceTest {
    @Inject
    lateinit var service: RuleSetService

    @Inject
    lateinit var unitTypeService: UnitTypeService

    @Inject
    lateinit var eventService: EventService

    private val eventCollector: EventConsumer<RuleSet> = mockk()
    private val request = RuleSetChangeRequest("name")

    init {
        every { eventCollector.created(any()) } just Runs
        every { eventCollector.updated(any()) } just Runs
        every { eventCollector.deleted(any()) } just Runs
    }

    @BeforeEach
    fun setup() {
        eventService.register(RuleSet::class, eventCollector)
    }

    @AfterEach
    fun teardown() {
        eventService.unregister(RuleSet::class, eventCollector)
        service.deleteAll()
        unitTypeService.deleteAll()
    }

    @Test
    fun testCrudOperations() {

        // CREATE
        val response = service.create(request)
        var reference = RuleSet(response.id, request.name, emptyList())
        assertEquals(reference, response)
        verify { eventCollector.created(reference) }

        // GETTER
        assertEquals(reference, service.get(reference.id))
        assertEquals(listOf(reference), service.getAll(Pageable.from(0)).content)

        // UPDATE
        val update = RuleSetChangeRequest("name-update")
        reference = RuleSet(response.id, update.name, emptyList())
        assertEquals(reference, service.update(reference.id, update))
        verify { eventCollector.updated(reference) }

        // DELETE
        service.delete(reference.id)
        verify { eventCollector.deleted(reference) }
        verify { eventCollector.hashCode() }
        confirmVerified(eventCollector)

        // EMPTY GETTER
        assertNull(service.get(0))
        assertEquals(emptyList<RuleSet>(), service.getAll(Pageable.from(0)).content)
    }

    @Test
    fun testUpdateWithInvalidId() {
        service.create(request)

        val updateResponse = service.update(99, request)
        val updateReference = RuleSet(updateResponse.id, request.name, emptyList())
        assertEquals(updateReference, updateResponse)
        verify { eventCollector.created(updateReference) }
    }


    @Test
    fun testValidation() {
        assertThrows<RuleSetNameValidationFailed> { service.create(RuleSetChangeRequest("")) }

        val response = service.create(request)
        assertThrows<RuleSetNameValidationFailed> { service.update(response.id, (RuleSetChangeRequest(""))) }
        service.delete(response.id)
    }

    @Test
    fun testUnitTypeRelations() {
        val ruleSet = service.create(request)
        val unitType = unitTypeService.create(UnitDefinitionChangeRequest("name", UnitType.INFANTRY, UnitType.CAVALRY, PointsRange(1, 3), PointsRange(1, 3), 4))

        val assigned = service.assignUnitType(ruleSet, unitType)
        Assertions.assertNotNull(assigned)
        assertEquals(listOf(unitType), assigned!!.unitDefinitions)
        verify { eventCollector.updated(assigned) }

        val response = service.get(assigned.id)
        Assertions.assertNotNull(response)
        assertEquals(assigned, response)

        val revoked = service.revokeUnitType(ruleSet, unitType)
        Assertions.assertNotNull(revoked)
        assertEquals(listOf<UnitDefinition>(), revoked!!.unitDefinitions)
        verify { eventCollector.updated(revoked) }
    }


    @Test
    fun testDeletionWithRelations() {
        val ruleSet = service.create(request)
        val unitType = unitTypeService.create(UnitDefinitionChangeRequest("name", UnitType.INFANTRY, UnitType.CAVALRY, PointsRange(1, 3), PointsRange(1, 3), 4))

        service.assignUnitType(ruleSet, unitType)

        val response = service.get(ruleSet.id)
        Assertions.assertNotNull(response)

        service.delete(response!!.id)
        verify { eventCollector.deleted(response) }
    }
}