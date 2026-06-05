package de.sambalmueslie.boardbuddy.workflow.battle.cmd

import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.workflow.api.WorkflowBattleAttackFrontRequest
import de.sambalmueslie.boardbuddy.workflow.battle.db.BattleData

class BattleCmdFrontAttack(
    val session: GameSession,
    val battle: BattleData,
    val request: WorkflowBattleAttackFrontRequest,
    val attacker: GameSessionPlayer,
    val defender: GameSessionPlayer,
) : BattleCommand {
}