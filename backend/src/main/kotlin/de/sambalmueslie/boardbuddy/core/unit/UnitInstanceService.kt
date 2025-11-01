package de.sambalmueslie.boardbuddy.core.unit

import de.sambalmueslie.boardbuddy.core.common.BaseEntityService
import de.sambalmueslie.boardbuddy.core.common.TimeProvider
import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.event.api.EventConsumer
import de.sambalmueslie.boardbuddy.core.unit.api.*
import de.sambalmueslie.boardbuddy.core.unit.db.UnitInstanceData
import de.sambalmueslie.boardbuddy.core.unit.db.UnitInstanceRepository
import de.sambalmueslie.openevent.common.PageableSequence
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class UnitInstanceService(
    repository: UnitInstanceRepository,

    private val typeService: UnitTypeService,

    eventService: EventService,
    private val timeProvider: TimeProvider
) : BaseEntityService<UnitInstance, UnitInstanceChangeRequest, UnitInstanceData>(repository, eventService, UnitInstance::class) {

    companion object {
        private val logger = LoggerFactory.getLogger(UnitInstanceService::class.java)
    }

    init {
        eventService.register(UnitType::class, object : EventConsumer<UnitType> {
            override fun created(obj: UnitType) {
                // intentionally left empty
            }

            override fun updated(obj: UnitType) {
                // intentionally left empty
            }

            override fun deleted(obj: UnitType) {
                PageableSequence() { repository.getByUnitTypeId(obj.id, it) }.forEach { delete(it) }
            }
        })
    }


    override fun convert(data: UnitInstanceData): UnitInstance {
        val type = typeService.get(data.unitTypeId) ?: throw CannotFindUnitType(data.unitTypeId)
        return data.convert(type)
    }

    override fun createData(request: UnitInstanceChangeRequest): UnitInstanceData {
        return UnitInstanceData(0, request.type.id, request.damage, request.health, request.level, timeProvider.currentTime())
    }

    override fun updateData(existing: UnitInstanceData, request: UnitInstanceChangeRequest): UnitInstanceData {
        return existing.update(request, timeProvider.currentTime())
    }

    override fun validate(request: UnitInstanceChangeRequest) {
        if (request.level <= 0) throw UnitInstanceLevelValidationFailed(request.level)
        if (request.damage <= 0 || !request.type.damagePoints.isWithin(request.damage)) throw UnitInstanceDamageValidationFailed(request.damage)
        if (request.health <= 0 || !request.type.healthPoints.isWithin(request.health)) throw UnitInstanceHealthValidationFailed(request.health)
    }

}