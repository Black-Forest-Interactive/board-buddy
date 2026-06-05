package de.sambalmueslie.boardbuddy.workflow.battle.db

import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.workflow.api.BattleStatus
import de.sambalmueslie.boardbuddy.workflow.api.BattleType
import de.sambalmueslie.boardbuddy.workflow.api.WorkflowBattleInvalidPlayer
import de.sambalmueslie.boardbuddy.workflow.api.WorkflowBattlePlayerIsNotActive

data class BattleData(
    val participant: List<BattleParticipantData>,
    val type: BattleType,
    var activePlayer: GameSessionPlayer,
    var status: BattleStatus,
    val fronts: MutableList<BattleFrontData> = mutableListOf(),
    val logEntries: MutableList<BattleLogEntryData> = mutableListOf(),

    var winner: GameSessionPlayer? = null,
) {

    fun validatePlayerIsActive(player: GameSessionPlayer) {
        if (player.player.id != activePlayer.player.id) throw WorkflowBattlePlayerIsNotActive(player.player.id)
    }

    fun getAndValidateParticipant(player: GameSessionPlayer): BattleParticipantData {
        return participant.find { it.matches(player) } ?: throw WorkflowBattleInvalidPlayer(player.player.id)
    }


}