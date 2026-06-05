package de.sambalmueslie.boardbuddy.workflow.battle.action

import de.sambalmueslie.boardbuddy.core.player.api.PlayerType
import de.sambalmueslie.boardbuddy.core.session.GameSessionService
import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.engine.GameEngine
import de.sambalmueslie.boardbuddy.engine.api.GameEntity
import de.sambalmueslie.boardbuddy.engine.api.UnitType
import de.sambalmueslie.boardbuddy.workflow.api.WorkflowBattleStartFailed
import de.sambalmueslie.boardbuddy.workflow.battle.api.BattleStatus
import de.sambalmueslie.boardbuddy.workflow.battle.api.BattleType
import de.sambalmueslie.boardbuddy.workflow.battle.cmd.BattleCmdStart
import de.sambalmueslie.boardbuddy.workflow.battle.db.BattleData
import de.sambalmueslie.boardbuddy.workflow.battle.db.BattleParticipantData
import jakarta.inject.Singleton

@Singleton
class BattleActionStart(
    private val sessionService: GameSessionService,
    private val engine: GameEngine,
) {

    companion object {
        private val aiPlayerUnitTypes = setOf(UnitType.INFANTRY, UnitType.MOUNTED, UnitType.ARTILLERY)
    }

    internal fun execute(cmd: BattleCmdStart): BattleData {
        val session = cmd.session
        val request = cmd.request
        val attacker = cmd.attacker
        val defender = cmd.defender

        val battleType = request.type
        val attackerUnits = determineUnits(session, attacker, request.attacker.armyCount, battleType)
        if (attackerUnits.isEmpty()) throw WorkflowBattleStartFailed(session.id)
        val defenderUnits = determineUnits(session, defender, request.defender.armyCount, battleType)

        val startPlayer = engine.determineStartPlayer(attacker, defender, battleType, request.isWalled)

        val participants = listOf(
            BattleParticipantData(attacker, request.attacker.armyCount, attackerUnits),
            BattleParticipantData(defender, request.defender.armyCount, defenderUnits)
        )
        return BattleData(participants, battleType, startPlayer, BattleStatus.INIT)
    }

    private fun determineUnits(session: GameSession, player: GameSessionPlayer, armyCount: Int, battleType: BattleType): MutableList<GameEntity> {
        val entities = sessionService.getAssignedEntities(session, player)
        val units = engine.determineAttackerUnits(player, armyCount, battleType, entities)
        if (player.player.type == PlayerType.HUMAN) return units.toMutableList()

        val definitions = session.ruleSet.unitDefinitions
        val relevantDefinitions = definitions.filter { aiPlayerUnitTypes.contains(it.unitType) }

        val gameUnits = entities.map { engine.getUnit(it) }.associateBy { it.type?.kind }

        return relevantDefinitions.map { d ->
            val existing = gameUnits[d.unitType]
            if (existing != null) return@map existing.entity

            val entity = engine.createUnit(player, d)
            sessionService.assignEntity(session, player.player, entity)
            entity
        }.toMutableList()
    }


}