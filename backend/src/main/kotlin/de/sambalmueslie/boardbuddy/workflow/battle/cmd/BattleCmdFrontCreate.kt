package de.sambalmueslie.boardbuddy.workflow.battle.cmd

import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.workflow.battle.api.WorkflowBattleCreateFrontRequest
import de.sambalmueslie.boardbuddy.workflow.battle.db.BattleData

class BattleCmdFrontCreate(
    val session: GameSession,
    val battle: BattleData,
    val request: WorkflowBattleCreateFrontRequest,
    val player: GameSessionPlayer
) : BattleCommand {
}