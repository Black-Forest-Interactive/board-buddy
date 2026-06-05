package de.sambalmueslie.boardbuddy.workflow.battle.action

import de.sambalmueslie.boardbuddy.workflow.battle.cmd.*
import de.sambalmueslie.boardbuddy.workflow.battle.db.BattleData
import jakarta.inject.Singleton

@Singleton
class BattleActionFactory(
    private val actionStart: BattleActionStart,
    private val actionCancel: BattleActionCancel,
    private val actionFinish: BattleActionFinish,
    private val actionFrontCreate: BattleActionFrontCreate,
    private val actionFrontAttack: BattleActionFrontAttack,
) {

    internal fun execute(cmd: BattleCommand): BattleData {
        return when (cmd) {
            is BattleCmdStart -> actionStart.execute(cmd)
            is BattleCmdCancel -> actionCancel.execute(cmd)
            is BattleCmdFinish -> actionFinish.execute(cmd)
            is BattleCmdFrontCreate -> actionFrontCreate.execute(cmd)
            is BattleCmdFrontAttack -> actionFrontAttack.execute(cmd)
            else -> throw RuntimeException("Unknown command type ${cmd.javaClass.name}")
        }
    }

}