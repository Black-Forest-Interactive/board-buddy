package de.sambalmueslie.boardbuddy.gateway.portal.api

import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.workflow.battle.api.BattleFront
import de.sambalmueslie.boardbuddy.workflow.battle.api.BattleLogEntry
import de.sambalmueslie.boardbuddy.workflow.battle.api.BattleParticipant
import de.sambalmueslie.boardbuddy.workflow.battle.api.BattleStatus

data class PortalBattle(
    val status: BattleStatus,
    val activePlayer: GameSessionPlayer,
    val myInfo: BattleParticipant,
    val opponentInfo: PortalBattleOpponent,
    val fronts: List<BattleFront>,
    val logEntries: List<BattleLogEntry>,
    val winner: GameSessionPlayer?,
)
