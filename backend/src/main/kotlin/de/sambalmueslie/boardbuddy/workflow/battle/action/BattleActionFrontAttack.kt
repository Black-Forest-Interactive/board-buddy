package de.sambalmueslie.boardbuddy.workflow.battle.action

import de.sambalmueslie.boardbuddy.engine.GameEngine
import de.sambalmueslie.boardbuddy.engine.api.Health
import de.sambalmueslie.boardbuddy.workflow.api.BattleActivity
import de.sambalmueslie.boardbuddy.workflow.api.WorkflowBattleFrontAttackInvalid
import de.sambalmueslie.boardbuddy.workflow.api.WorkflowBattleInvalidFrontIndex
import de.sambalmueslie.boardbuddy.workflow.battle.cmd.BattleCmdFrontAttack
import de.sambalmueslie.boardbuddy.workflow.battle.db.BattleData
import de.sambalmueslie.boardbuddy.workflow.battle.db.BattleFrontUnitData
import de.sambalmueslie.boardbuddy.workflow.battle.db.BattleLogEntryData
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class BattleActionFrontAttack(
    private val gameEngine: GameEngine,
) : BaseBattleActiveAction<BattleCmdFrontAttack>() {

    companion object {
        private val logger = LoggerFactory.getLogger(BattleActionFrontAttack::class.java)
    }

    override fun getBattle(cmd: BattleCmdFrontAttack) = cmd.battle
    override fun getPlayer(cmd: BattleCmdFrontAttack) = cmd.attacker

    override fun process(cmd: BattleCmdFrontAttack): BattleData {
        val data = cmd.battle
        val attacker = cmd.attacker
        val defender = cmd.defender
        val request = cmd.request

        val attackParticipant = data.getAndValidateParticipant(attacker)
        val defendParticipant = data.getAndValidateParticipant(defender)

        val front = data.fronts.find { f -> f.index == request.frontIndex } ?: throw WorkflowBattleInvalidFrontIndex(request.frontIndex)
        if (front.units.any { u -> u.player.entity == attacker.entity }) throw WorkflowBattleFrontAttackInvalid(request.frontIndex)

        val attackEntity = attackParticipant.getAndValidateUnitEntity(request.entityId)
        val health = gameEngine.getComponent(attackEntity, Health::class) ?: return data
        val attackUnit = BattleFrontUnitData(attacker, attackEntity, health.amount)
        attackParticipant.units.remove(attackEntity)
        front.units.add(attackUnit)

        val defendUnit = front.units.find { u -> u.player.entity == defendParticipant.player.entity } ?: return data
        val actions = gameEngine.combat(attackUnit, defendUnit)

        val logEntry = BattleLogEntryData(attacker, BattleActivity.ATTACK_FRONT, actions)
        data.logEntries.add(logEntry)
        return data
    }
}