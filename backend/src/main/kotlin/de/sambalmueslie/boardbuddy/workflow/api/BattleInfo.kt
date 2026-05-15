package de.sambalmueslie.boardbuddy.workflow.api

import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer

data class BattleInfo(
    val participant: List<BattleParticipantInfo>,
    val activePlayer: GameSessionPlayer,
)
