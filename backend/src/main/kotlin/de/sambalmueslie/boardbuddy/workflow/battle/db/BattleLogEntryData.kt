package de.sambalmueslie.boardbuddy.workflow.battle.db

import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.engine.api.CombatAction
import de.sambalmueslie.boardbuddy.workflow.battle.api.BattleActivity

data class BattleLogEntryData(
    val player: GameSessionPlayer,
    val activity: BattleActivity,
    val actions: List<CombatAction>
)
