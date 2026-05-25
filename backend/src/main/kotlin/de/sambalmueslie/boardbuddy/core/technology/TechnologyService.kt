package de.sambalmueslie.boardbuddy.core.technology

import de.sambalmueslie.boardbuddy.common.BaseEntityService
import de.sambalmueslie.boardbuddy.common.TimeProvider
import de.sambalmueslie.boardbuddy.common.findByIdOrNull
import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.technology.api.*
import de.sambalmueslie.boardbuddy.core.technology.db.TechnologyData
import de.sambalmueslie.boardbuddy.core.technology.db.TechnologyRepository
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class TechnologyService(
    private val repository: TechnologyRepository,
    private val effectService: TechnologyEffectService,
    eventService: EventService,
    private val timeProvider: TimeProvider
) : BaseEntityService<Technology, TechnologyChangeRequest, TechnologyData>(repository, eventService, Technology::class) {


    companion object {
        private val logger = LoggerFactory.getLogger(TechnologyService::class.java)
    }

    fun assignTechnologyEffect(technology: Technology, effect: TechnologyEffect): Technology? {
        return assignTechnologyEffect(technology.id, effect)
    }

    fun assignTechnologyEffect(technologyId: Long, effect: TechnologyEffect): Technology? {
        val data = repository.findByIdOrNull(technologyId) ?: return null
        effectService.assign(data, effect)
        val result = convert(data)
        notifyUpdate(result)
        return result
    }

    fun revokeTechnologyEffect(technology: Technology, effect: TechnologyEffect): Technology? {
        return revokeTechnologyEffect(technology.id, effect)
    }

    fun revokeTechnologyEffect(technologyId: Long, effect: TechnologyEffect): Technology? {
        val data = repository.findByIdOrNull(technologyId) ?: return null
        effectService.revoke(data, effect)
        val result = convert(data)
        notifyUpdate(result)
        return result
    }


    override fun convert(data: TechnologyData): Technology {
        return data.convert(effectService.getAssignedTechnologyEffects(data))
    }

    override fun createData(request: TechnologyChangeRequest): TechnologyData {
        return TechnologyData(0, request.name, request.description, request.imageUrl, request.tier, timeProvider.currentTime())
    }

    override fun updateData(existing: TechnologyData, request: TechnologyChangeRequest): TechnologyData {
        return existing.update(request, timeProvider.currentTime())
    }

    override fun validate(request: TechnologyChangeRequest) {
        if (request.name.isBlank()) throw TechnologyNameValidationFailed(request.name)
        if (request.tier <= 0) throw TechnologyTierValidationFailed(request.tier)
    }

    override fun deleteDependencies(data: TechnologyData) {
        effectService.revokeAll(data)
    }
}