package de.sambalmueslie.boardbuddy.workflow.battle.action

import de.sambalmueslie.boardbuddy.workflow.battle.api.BattleStatus
import de.sambalmueslie.boardbuddy.workflow.battle.cmd.BattleCmdCancel
import de.sambalmueslie.boardbuddy.workflow.battle.db.BattleData
import jakarta.inject.Singleton

@Singleton
class BattleActionCancel : BaseBattleAction<BattleCmdCancel>() {

    override fun execute(cmd: BattleCmdCancel): BattleData {
        val battle = cmd.battle
        battle.status = BattleStatus.CANCELED
        updateWinner(battle)
        return battle
    }

}