package de.sambalmueslie.boardbuddy.workflow.battle.api

import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.engine.api.GameUnit

data class BattleFrontUnit(
    val player: GameSessionPlayer,
    val unit: GameUnit,
    val currentHealth: Int,
)
