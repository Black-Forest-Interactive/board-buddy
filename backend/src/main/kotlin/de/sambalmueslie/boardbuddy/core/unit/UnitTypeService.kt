package de.sambalmueslie.boardbuddy.core.unit

import de.sambalmueslie.boardbuddy.common.BaseEntityService
import de.sambalmueslie.boardbuddy.common.TimeProvider
import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.unit.api.*
import de.sambalmueslie.boardbuddy.core.unit.db.UnitDefinitionData
import de.sambalmueslie.boardbuddy.core.unit.db.UnitDefinitionRepository
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class UnitTypeService(
    repository: UnitDefinitionRepository,
    eventService: EventService,
    private val timeProvider: TimeProvider
) : BaseEntityService<UnitDefinition, UnitDefinitionChangeRequest, UnitDefinitionData>(repository, eventService, UnitDefinition::class) {

    companion object {
        private val logger = LoggerFactory.getLogger(UnitTypeService::class.java)
    }

    override fun convert(data: UnitDefinitionData): UnitDefinition {
        return data.convert()
    }

    override fun createData(request: UnitDefinitionChangeRequest): UnitDefinitionData {
        return UnitDefinitionData(
            0,
            request.name,
            request.unitType,
            request.counterClass,
            request.damagePoints.min,
            request.damagePoints.max,
            request.healthPoints.min,
            request.healthPoints.max,
            request.maxLevel,
            timeProvider.currentTime()
        )
    }

    override fun updateData(existing: UnitDefinitionData, request: UnitDefinitionChangeRequest): UnitDefinitionData {
        return existing.update(request, timeProvider.currentTime())
    }

    override fun validate(request: UnitDefinitionChangeRequest) {
        if (request.name.isBlank()) throw UnitDefinitionNameValidationFailed(request.name)
        if (request.counterClass == request.unitType) throw UnitDefinitionCounterClassValidationFailed(request.counterClass)
        if (request.damagePoints.min <= 0) throw UnitDefinitionDamageValidationFailed(request.damagePoints)
        if (request.damagePoints.min > request.damagePoints.max) throw UnitDefinitionDamageValidationFailed(request.damagePoints)
        if (request.healthPoints.min <= 0) throw UnitDefinitionHealthValidationFailed(request.healthPoints)
        if (request.healthPoints.min > request.healthPoints.max) throw UnitDefinitionHealthValidationFailed(request.damagePoints)
        if (request.maxLevel <= 0) throw UnitDefinitionLevelValidationFailed(request.maxLevel)
    }
}