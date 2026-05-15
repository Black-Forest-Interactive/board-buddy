package de.sambalmueslie.boardbuddy.engine.system

import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.engine.api.GameEntity
import de.sambalmueslie.boardbuddy.engine.api.GameSystem
import de.sambalmueslie.boardbuddy.engine.api.Government
import de.sambalmueslie.boardbuddy.engine.api.GovernmentType
import de.sambalmueslie.boardbuddy.engine.component.GameComponentModelService
import de.sambalmueslie.boardbuddy.workflow.api.BattleType
import jakarta.inject.Singleton

@Singleton
class BattleHandSystem(
    componentModelService: GameComponentModelService,
) : GameSystem {

    companion object {
        private const val INITIAL_HAND_SIZE = 3
        private val DEFAULT_GOVERNMENT = Government(GovernmentType.DESPOTISM)
    }

    private val governmentModel = componentModelService.get(Government::class)

    fun determine(participant: GameSessionPlayer, armyCount: Int, type: BattleType, units: List<GameEntity>, isAttacker: Boolean): List<GameEntity> {
        val isDefendingCity = type == BattleType.ARMY_VS_CITY && !isAttacker
        val isFundamentalism = getGovernment(participant).type == GovernmentType.FUNDAMENTALISM
        val handSize = calculateBattleHandSize(armyCount, isDefendingCity, isFundamentalism)
        return units.shuffled().take(handSize)
    }

    private fun getGovernment(participant: GameSessionPlayer): Government {
        return governmentModel.get(participant.entity) ?: DEFAULT_GOVERNMENT
    }

    private fun calculateBattleHandSize(armyCount: Int, isDefendingCity: Boolean, isFundamentalism: Boolean): Int {
        var handSize = INITIAL_HAND_SIZE
        handSize += (armyCount - 1) * 2
        if (isDefendingCity) handSize += 3
        if (isFundamentalism) handSize += 1
        return handSize
    }
}