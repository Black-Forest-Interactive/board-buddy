package de.sambalmueslie.boardbuddy.core.workflow.api

import de.sambalmueslie.boardbuddy.core.player.api.Player

data class Battle(
    val participant: List<BattleParticipant>,
    val activePlayer: Player,
)
