package de.sambalmueslie.boardbuddy.workflow.battle.cmd

import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.workflow.battle.api.WorkflowBattleStartRequest

class BattleCmdStart(
    val session: GameSession,
    val request: WorkflowBattleStartRequest,
    val attacker: GameSessionPlayer,
    val defender: GameSessionPlayer
) : BattleCommand {
}