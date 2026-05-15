package de.sambalmueslie.boardbuddy.workflow.api

import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer

data class Battle(
    val participant: List<BattleParticipant>,
    val fronts: List<BattleFront>,
    val logEntries: List<BattleLogEntry>,
    val activePlayer: GameSessionPlayer,
    val status: BattleStatus,
    val winner: GameSessionPlayer? = null,
)
