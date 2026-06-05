package de.sambalmueslie.boardbuddy.core.nation

import de.sambalmueslie.boardbuddy.common.BaseEntityService
import de.sambalmueslie.boardbuddy.common.TimeProvider
import de.sambalmueslie.boardbuddy.common.findByIdOrNull
import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.nation.api.Nation
import de.sambalmueslie.boardbuddy.core.nation.api.NationChangeRequest
import de.sambalmueslie.boardbuddy.core.nation.api.NationEffect
import de.sambalmueslie.boardbuddy.core.nation.api.NationNameValidationFailed
import de.sambalmueslie.boardbuddy.core.nation.db.NationData
import de.sambalmueslie.boardbuddy.core.nation.db.NationRepository
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class NationService(
    private val repository: NationRepository,
    private val effectService: NationEffectService,
    eventService: EventService,
    private val timeProvider: TimeProvider
) : BaseEntityService<Nation, NationChangeRequest, NationData>(repository, eventService, Nation::class) {


    companion object {
        private val logger = LoggerFactory.getLogger(NationService::class.java)
        private const val AI_NATION_NAME = "Barbarian"
    }

    fun assignNationEffect(nation: Nation, effect: NationEffect): Nation? {
        return assignNationEffect(nation.id, effect)
    }

    fun assignNationEffect(nationId: Long, effect: NationEffect): Nation? {
        val data = repository.findByIdOrNull(nationId) ?: return null
        effectService.assign(data, effect)
        val result = convert(data)
        notifyUpdate(result)
        return result
    }

    fun revokeNationEffect(nation: Nation, effect: NationEffect): Nation? {
        return revokeNationEffect(nation.id, effect)
    }

    fun revokeNationEffect(nationId: Long, effect: NationEffect): Nation? {
        val data = repository.findByIdOrNull(nationId) ?: return null
        effectService.revoke(data, effect)
        val result = convert(data)
        notifyUpdate(result)
        return result
    }


    override fun convert(data: NationData): Nation {
        return data.convert(effectService.getAssignedNationEffects(data))
    }

    override fun createData(request: NationChangeRequest): NationData {
        return NationData(0, request.name, request.description, request.imageUrl, timeProvider.currentTime())
    }

    override fun updateData(existing: NationData, request: NationChangeRequest): NationData {
        return existing.update(request, timeProvider.currentTime())
    }

    override fun validate(request: NationChangeRequest) {
        if (request.name.isBlank()) throw NationNameValidationFailed(request.name)
    }

    override fun deleteDependencies(data: NationData) {
        effectService.revokeAll(data)
    }


    fun getAiNation(): Nation {
        val existing = repository.findByName(AI_NATION_NAME)
        if (existing != null) return convert(existing)

        return create(NationChangeRequest(AI_NATION_NAME, "", ""))
    }
}