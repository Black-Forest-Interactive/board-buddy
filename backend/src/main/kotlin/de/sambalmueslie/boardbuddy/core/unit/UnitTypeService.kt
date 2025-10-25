package de.sambalmueslie.boardbuddy.core.unit

import de.sambalmueslie.boardbuddy.core.common.BaseEntityService
import de.sambalmueslie.boardbuddy.core.common.TimeProvider
import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.unit.api.*
import de.sambalmueslie.boardbuddy.core.unit.db.UnitTypeData
import de.sambalmueslie.boardbuddy.core.unit.db.UnitTypeRepository
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class UnitTypeService(
    repository: UnitTypeRepository,
    eventService: EventService,
    private val timeProvider: TimeProvider
) : BaseEntityService<UnitType, UnitTypeChangeRequest, UnitTypeData>(repository, eventService, UnitType::class) {

    companion object {
        private val logger = LoggerFactory.getLogger(UnitTypeService::class.java)
    }

    override fun convert(data: UnitTypeData): UnitType {
        return data.convert()
    }

    override fun createData(request: UnitTypeChangeRequest): UnitTypeData {
        return UnitTypeData(
            0,
            request.name,
            request.unitClass,
            request.counterClass,
            request.damagePoints.min,
            request.damagePoints.max,
            request.healthPoints.min,
            request.healthPoints.max,
            request.maxLevel,
            timeProvider.currentTime()
        )
    }

    override fun updateData(existing: UnitTypeData, request: UnitTypeChangeRequest): UnitTypeData {
        return existing.update(request, timeProvider.currentTime())
    }

    override fun validate(request: UnitTypeChangeRequest) {
        if (request.name.isBlank()) throw UnitTypeNameValidationFailed(request.name)
        if (request.counterClass == request.unitClass) throw UnitTypeCounterClassValidationFailed(request.counterClass)
        if (request.damagePoints.min <= 0) throw UnitTypeDamageValidationFailed(request.damagePoints)
        if (request.damagePoints.min > request.damagePoints.max) throw UnitTypeDamageValidationFailed(request.damagePoints)
        if (request.healthPoints.min <= 0) throw UnitTypeHealthValidationFailed(request.healthPoints)
        if (request.healthPoints.min > request.healthPoints.max) throw UnitTypeHealthValidationFailed(request.damagePoints)
        if (request.maxLevel <= 0) throw UnitTypeLevelValidationFailed(request.maxLevel)
    }
}