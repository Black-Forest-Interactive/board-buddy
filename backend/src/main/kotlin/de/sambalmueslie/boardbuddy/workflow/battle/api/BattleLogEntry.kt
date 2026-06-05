package de.sambalmueslie.boardbuddy.workflow.battle.api

import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.engine.api.CombatAction

data class BattleLogEntry(
    val player: GameSessionPlayer,
    val activity: BattleActivity,
    val actions: List<CombatAction>
)
