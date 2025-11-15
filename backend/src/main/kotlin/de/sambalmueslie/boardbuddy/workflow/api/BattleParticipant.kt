package de.sambalmueslie.boardbuddy.workflow.api

import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.engine.api.GameEntity

data class BattleParticipant(
    val player: Player,
    val units: List<GameEntity>,
    val fronts: List<BattleFront>,
)
