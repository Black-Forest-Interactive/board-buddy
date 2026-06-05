package de.sambalmueslie.boardbuddy.workflow.battle.action

import de.sambalmueslie.boardbuddy.core.session.GameSessionService
import de.sambalmueslie.boardbuddy.engine.GameEngine
import de.sambalmueslie.boardbuddy.workflow.api.BattleStatus
import de.sambalmueslie.boardbuddy.workflow.api.WorkflowBattleStartFailed
import de.sambalmueslie.boardbuddy.workflow.battle.cmd.BattleCmdStart
import de.sambalmueslie.boardbuddy.workflow.battle.db.BattleData
import de.sambalmueslie.boardbuddy.workflow.battle.db.BattleParticipantData
import jakarta.inject.Singleton

@Singleton
class BattleActionStart(
    private val sessionService: GameSessionService,
    private val gameEngine: GameEngine,
) {

    internal fun execute(cmd: BattleCmdStart): BattleData{
        val session= cmd.session
        val request = cmd.request
        val attacker = cmd.attacker
        val defender = cmd.defender

        val battleType = request.type
        val attackerUnits = sessionService.getAssignedEntities(session, attacker).let { gameEngine.determineAttackerUnits(attacker, request.attacker.armyCount, battleType, it) }.toMutableList()
        if (attackerUnits.isEmpty()) throw WorkflowBattleStartFailed(session.id)
        val defenderUnits = sessionService.getAssignedEntities(session, defender).let { gameEngine.determineDefenderUnits(defender, request.defender.armyCount, battleType, it) }.toMutableList()

        val startPlayer = gameEngine.determineStartPlayer(attacker, defender, battleType, request.isWalled)

        val participants = listOf(
            BattleParticipantData(attacker, request.attacker.armyCount, attackerUnits),
            BattleParticipantData(defender, request.defender.armyCount, defenderUnits)
        )
        return BattleData(participants, battleType, startPlayer, BattleStatus.INIT)
    }


}