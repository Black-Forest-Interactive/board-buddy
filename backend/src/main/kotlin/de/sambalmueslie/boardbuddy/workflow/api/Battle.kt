package de.sambalmueslie.boardbuddy.workflow.api

import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer

data class Battle(
    val participant: List<BattleParticipant>,
    val activePlayer: GameSessionPlayer,
)
