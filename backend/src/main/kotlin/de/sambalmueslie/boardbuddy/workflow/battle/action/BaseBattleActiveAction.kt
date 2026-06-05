package de.sambalmueslie.boardbuddy.workflow.battle.action

import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.workflow.battle.api.BattleStatus
import de.sambalmueslie.boardbuddy.workflow.battle.cmd.BattleCommand
import de.sambalmueslie.boardbuddy.workflow.battle.db.BattleData

abstract class BaseBattleActiveAction<T : BattleCommand> : BaseBattleAction<T>() {

    final override fun execute(cmd: T): BattleData {
        val battle = getBattle(cmd)
        val player = getPlayer(cmd)

        battle.validatePlayerIsActive(player)

        val result = process(cmd)

        updateStatus(battle, player)

        if (result.status != BattleStatus.FINISHED) {
            switchActivePlayer(result, player)
        }
        return result
    }


    abstract fun getBattle(cmd: T): BattleData
    abstract fun getPlayer(cmd: T): GameSessionPlayer
    abstract fun process(cmd: T): BattleData

    private fun updateStatus(battle: BattleData, currentPlayer: GameSessionPlayer) {
        val allEmpty = battle.participant.all { it.units.isEmpty() }
        val nextIsEmpty = if (!allEmpty) {
            val currentIndex = battle.participant.indexOfFirst { it.matches(currentPlayer) }
            val nextIndex = if (currentIndex >= battle.participant.size - 1) 0 else currentIndex + 1
            battle.participant[nextIndex].units.isEmpty()
        } else false

        battle.status = if (allEmpty || nextIsEmpty) BattleStatus.FINISHED else BattleStatus.ONGOING
        updateWinner(battle)
    }


}