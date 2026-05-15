package de.sambalmueslie.boardbuddy.engine.system

import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.engine.api.GameSystem
import de.sambalmueslie.boardbuddy.workflow.api.BattleType
import jakarta.inject.Singleton

@Singleton
class StartPlayerSystem : GameSystem {
    fun determine(attacker: GameSessionPlayer, defender: GameSessionPlayer, type: BattleType, isWalled: Boolean): GameSessionPlayer {
        return when (type) {
            BattleType.ARMY_VS_ARMY -> defender
            BattleType.ARMY_VS_CITY ->  if (isWalled) attacker else defender
            BattleType.ARMY_VS_BARBARIANS -> defender
        }
    }
}