package de.sambalmueslie.boardbuddy.core.technology

import de.sambalmueslie.boardbuddy.common.TimeProvider
import de.sambalmueslie.boardbuddy.core.technology.api.TechnologyEffect
import de.sambalmueslie.boardbuddy.core.technology.db.TechnologyData
import de.sambalmueslie.boardbuddy.core.technology.db.TechnologyEffectUnitUnlockData
import de.sambalmueslie.boardbuddy.core.technology.db.TechnologyEffectUnitUnlockRepository
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class TechnologyEffectService(
    private val repository: TechnologyEffectUnitUnlockRepository,
    private val timeProvider: TimeProvider
) {

    companion object {
        private val logger = LoggerFactory.getLogger(TechnologyEffectService::class.java)
    }

    internal fun assign(technology: TechnologyData, effect: TechnologyEffect) {
        return when (effect) {
            is TechnologyEffect.UnitUnlock -> assignUnitUnlock(technology, effect)
        }
    }

    internal fun revoke(technology: TechnologyData, effect: TechnologyEffect) {
        return when (effect) {
            is TechnologyEffect.UnitUnlock -> revokeUnitUnlock(technology, effect)
        }
    }


    private fun assignUnitUnlock(technology: TechnologyData, effect: TechnologyEffect.UnitUnlock) {
        val existing = repository.findByTechnologyId(technology.id).find { it.unitType == effect.unitType && it.unitLevel == effect.unitLevel }
        if (existing != null) return

        repository.save(TechnologyEffectUnitUnlockData(0, technology.id, effect.unitType, effect.unitLevel,timeProvider.currentTime() ))
    }

    private fun revokeUnitUnlock(technology: TechnologyData, effect: TechnologyEffect.UnitUnlock) {
        val existing = repository.findByTechnologyId(technology.id).find { it.unitType == effect.unitType && it.unitLevel == effect.unitLevel } ?: return
        repository.delete(existing)
    }

    internal fun getAssignedTechnologyEffects(data: TechnologyData): List<TechnologyEffect> {
        return repository.findByTechnologyId(data.id).map { it.convert() }
    }

    internal fun revokeAll(data: TechnologyData) {
        repository.deleteByTechnologyId(data.id)
    }
}