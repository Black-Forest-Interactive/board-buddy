package de.sambalmueslie.boardbuddy.workflow.battle

import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.engine.api.GameEntity

data class BattlePlayerData(
    val player: Player,
    val entity: GameEntity
)
