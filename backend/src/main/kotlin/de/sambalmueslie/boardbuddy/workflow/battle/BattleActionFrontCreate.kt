package de.sambalmueslie.boardbuddy.workflow.battle

import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.engine.GameEngine
import de.sambalmueslie.boardbuddy.engine.api.GameEntity
import de.sambalmueslie.boardbuddy.engine.api.Health
import de.sambalmueslie.boardbuddy.workflow.api.BattleStatus
import de.sambalmueslie.boardbuddy.workflow.api.WorkflowBattleCreateFrontRequest
import de.sambalmueslie.boardbuddy.workflow.api.WorkflowBattleInvalidFrontIndex
import de.sambalmueslie.boardbuddy.workflow.api.WorkflowBattleUnitNotExisting
import jakarta.inject.Singleton

@Singleton
class BattleActionFrontCreate(
    private val gameEngine: GameEngine,
) {
    internal fun process(session: GameSession, data: BattleData, request: WorkflowBattleCreateFrontRequest, player: GameSessionPlayer): BattleData {
        val participant = data.getAndValidateParticipant(player)
        val unit = participant.getAndValidateUnitEntity(request.entityId)
        participant.units.remove(unit)

        val index = (data.fronts.lastOrNull()?.index ?: 0) + 1

        val front = createFront(data, participant, unit, index) ?: return data
        data.fronts.add(front)
        data.status = BattleStatus.ONGOING
        return data
    }

    private fun createFront(data: BattleData, participant: BattleParticipantData, unit: GameEntity, index: Int): BattleFrontData? {
        val availableUnits = participant.units
        if (!availableUnits.any { it == unit }) throw WorkflowBattleUnitNotExisting(unit)

        val existing = data.fronts.find { it.index == index }
        if (existing != null) throw WorkflowBattleInvalidFrontIndex(index)

        val health = gameEngine.getComponent(unit, Health::class) ?: return null
        val unit = BattleFrontUnitData(participant.player, unit, health.amount)
        return BattleFrontData(index, mutableListOf(unit))
    }

}