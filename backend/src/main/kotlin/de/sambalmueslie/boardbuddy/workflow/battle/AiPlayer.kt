package de.sambalmueslie.boardbuddy.workflow.battle

import de.sambalmueslie.boardbuddy.core.player.api.PlayerType
import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.engine.GameEngine
import de.sambalmueslie.boardbuddy.engine.api.*
import de.sambalmueslie.boardbuddy.workflow.battle.api.BattleStatus
import de.sambalmueslie.boardbuddy.workflow.battle.api.WorkflowBattleAttackFrontRequest
import de.sambalmueslie.boardbuddy.workflow.battle.api.WorkflowBattleCreateFrontRequest
import de.sambalmueslie.boardbuddy.workflow.battle.cmd.BattleCommand
import de.sambalmueslie.boardbuddy.workflow.battle.cmd.BattleCmdFrontAttack
import de.sambalmueslie.boardbuddy.workflow.battle.cmd.BattleCmdFrontCreate
import de.sambalmueslie.boardbuddy.workflow.battle.db.BattleData
import de.sambalmueslie.boardbuddy.workflow.battle.db.BattleParticipantData
import jakarta.inject.Singleton

@Singleton
class AiPlayer(private val gameEngine: GameEngine) {

    fun play(session: GameSession, battle: BattleData): BattleCommand? {
        if (battle.status == BattleStatus.FINISHED || battle.status == BattleStatus.CANCELED) return null
        val aiPlayer = battle.activePlayer
        if (aiPlayer.player.type != PlayerType.AI) return null

        val aiParticipant = battle.participant.find { it.matches(aiPlayer) } ?: return null
        if (aiParticipant.units.isEmpty()) return null
        val opponentParticipant = battle.participant.find { !it.matches(aiPlayer) } ?: return null

        return tryAttackFront(session, battle, aiPlayer, aiParticipant, opponentParticipant)
            ?: createFront(session, battle, aiPlayer, aiParticipant, opponentParticipant)
    }

    private fun tryAttackFront(
        session: GameSession,
        battle: BattleData,
        aiPlayer: GameSessionPlayer,
        aiParticipant: BattleParticipantData,
        opponentParticipant: BattleParticipantData,
    ): BattleCmdFrontAttack? {
        val attackableFronts = battle.fronts.filter { front ->
            front.units.any { it.player.entity == opponentParticipant.player.entity } &&
                front.units.none { it.player.entity == aiPlayer.entity }
        }
        if (attackableFronts.isEmpty()) return null

        for (front in attackableFronts) {
            val opponentFrontUnit = front.units.first { it.player.entity == opponentParticipant.player.entity }
            val opponentType = gameEngine.getComponent(opponentFrontUnit.unit, Type::class)?.kind
            val opponentDamage = gameEngine.getComponent(opponentFrontUnit.unit, Damage::class)?.amount ?: 0

            val selectedUnit = aiParticipant.units.firstOrNull { entity ->
                val counterType = gameEngine.getComponent(entity, CounterType::class)?.kind
                counterType != null && counterType == opponentType
            } ?: aiParticipant.units.firstOrNull { entity ->
                val health = gameEngine.getComponent(entity, Health::class)?.amount ?: 0
                health > opponentDamage
            } ?: aiParticipant.units.firstOrNull() ?: continue

            return BattleCmdFrontAttack(
                session, battle,
                WorkflowBattleAttackFrontRequest(
                    attackerId = aiPlayer.player.id,
                    defenderId = opponentParticipant.player.player.id,
                    entityId = selectedUnit,
                    frontIndex = front.index
                ),
                aiPlayer,
                opponentParticipant.player
            )
        }
        return null
    }

    private fun createFront(
        session: GameSession,
        battle: BattleData,
        aiPlayer: GameSessionPlayer,
        aiParticipant: BattleParticipantData,
        opponentParticipant: BattleParticipantData,
    ): BattleCmdFrontCreate? {
        if (aiParticipant.units.isEmpty()) return null

        val opponentMaxDamage = battle.fronts
            .flatMap { it.units }
            .filter { it.player.entity == opponentParticipant.player.entity }
            .mapNotNull { gameEngine.getComponent(it.unit, Damage::class)?.amount }
            .maxOrNull() ?: 0

        val selectedUnit = (if (opponentMaxDamage > 0) {
            aiParticipant.units.firstOrNull { entity ->
                val health = gameEngine.getComponent(entity, Health::class)?.amount ?: 0
                health > opponentMaxDamage
            }
        } else null) ?: aiParticipant.units.first()

        return BattleCmdFrontCreate(
            session, battle,
            WorkflowBattleCreateFrontRequest(
                playerId = aiPlayer.player.id,
                entityId = selectedUnit
            ),
            aiPlayer
        )
    }
}
