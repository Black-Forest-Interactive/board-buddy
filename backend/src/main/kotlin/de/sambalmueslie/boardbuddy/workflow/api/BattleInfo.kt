package de.sambalmueslie.boardbuddy.workflow.api

import de.sambalmueslie.boardbuddy.core.player.api.Player

data class BattleInfo(
    val participant: List<BattleParticipantInfo>,
    val activePlayer: Player,
)
