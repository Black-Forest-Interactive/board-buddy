package de.sambalmueslie.boardbuddy.workflow.battle.action

import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.workflow.api.BattleStatus
import de.sambalmueslie.boardbuddy.workflow.battle.cmd.BattleCommand
import de.sambalmueslie.boardbuddy.workflow.battle.db.BattleData

abstract class BaseBattleAction<T : BattleCommand> : BattleAction<T> {
    protected fun updateWinner(battle: BattleData) {
        if (battle.status == BattleStatus.FINISHED || battle.status == BattleStatus.CANCELED) {
            val playerRemainingHealth = battle.fronts.flatMap { it.units }.groupBy { it.player }
                .mapValues { it.value.sumOf { u -> u.currentHealth } }
                .filter { it.value > 0 }
            battle.winner = playerRemainingHealth.maxByOrNull { it.value }?.key
        }
    }

    protected  fun switchActivePlayer(data: BattleData, player: GameSessionPlayer) {
        val currentIndex = data.participant.indexOfFirst { it.matches(player) }
        val nextIndex = if (currentIndex >= data.participant.size - 1) 0 else currentIndex + 1
        val nextPlayer = data.participant[nextIndex].player
        data.activePlayer = nextPlayer
    }
}