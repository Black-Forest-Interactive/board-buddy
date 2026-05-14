package de.sambalmueslie.boardbuddy.workflow.api

import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.engine.api.GameUnit

data class BattleParticipantInfo(
    val player: Player,
    val units: List<GameUnit>,
    val fronts: List<BattleFrontInfo>,
)
