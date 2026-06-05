package de.sambalmueslie.boardbuddy.workflow.battle.action

import de.sambalmueslie.boardbuddy.workflow.battle.cmd.BattleCommand
import de.sambalmueslie.boardbuddy.workflow.battle.db.BattleData

internal interface BattleAction<T : BattleCommand> {
    fun execute(cmd: T): BattleData
}