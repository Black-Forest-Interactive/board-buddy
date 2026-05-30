package de.sambalmueslie.boardbuddy.gateway.portal.api

import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.workflow.api.*

data class PortalBattle(
    val status: BattleStatus,
    val activePlayer: GameSessionPlayer,
    val myInfo: BattleParticipant,
    val opponentInfo: PortalBattleOpponent,
    val fronts: List<BattleFront>,
    val logEntries: List<BattleLogEntry>,
    val winner: GameSessionPlayer?,
)
