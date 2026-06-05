package de.sambalmueslie.boardbuddy.workflow.battle.cmd

import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.workflow.battle.db.BattleData

class BattleCmdFinish(
    val session: GameSession,
    val battle: BattleData
) : BattleCommand {
}