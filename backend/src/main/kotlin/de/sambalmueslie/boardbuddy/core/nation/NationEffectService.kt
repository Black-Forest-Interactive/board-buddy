package de.sambalmueslie.boardbuddy.core.nation

import de.sambalmueslie.boardbuddy.common.TimeProvider
import de.sambalmueslie.boardbuddy.core.nation.api.NationEffect
import de.sambalmueslie.boardbuddy.core.nation.db.NationData
import de.sambalmueslie.boardbuddy.core.nation.db.NationEffectInitialGovernmentData
import de.sambalmueslie.boardbuddy.core.nation.db.NationEffectInitialGovernmentRepository
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class NationEffectService(
    private val repository: NationEffectInitialGovernmentRepository,
    private val timeProvider: TimeProvider
) {

    companion object {
        private val logger = LoggerFactory.getLogger(NationEffectService::class.java)
    }

    internal fun assign(nation: NationData, effect: NationEffect) {
        return when (effect) {
            is NationEffect.InitialGovernment -> assignInitialGovernment(nation, effect)
        }
    }

    internal fun revoke(nation: NationData, effect: NationEffect) {
        return when (effect) {
            is NationEffect.InitialGovernment -> revokeInitialGovernment(nation, effect)
        }
    }


    private fun assignInitialGovernment(nation: NationData, effect: NationEffect.InitialGovernment) {
        val existing = repository.findByNationId(nation.id).find { it.type == effect.type }
        if (existing != null) return

        repository.save(NationEffectInitialGovernmentData(0, nation.id, effect.type, timeProvider.currentTime()))
    }

    private fun revokeInitialGovernment(nation: NationData, effect: NationEffect.InitialGovernment) {
        val existing = repository.findByNationId(nation.id).find { it.type == effect.type } ?: return
        repository.delete(existing)
    }

    internal fun getAssignedNationEffects(data: NationData): List<NationEffect> {
        return repository.findByNationId(data.id).map { it.convert() }
    }

    internal fun revokeAll(data: NationData) {
        repository.deleteByNationId(data.id)
    }
}