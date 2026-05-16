package de.sambalmueslie.boardbuddy.workflow.battle

import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.engine.GameEngine
import de.sambalmueslie.boardbuddy.engine.api.GameEntity
import de.sambalmueslie.boardbuddy.engine.api.Health
import de.sambalmueslie.boardbuddy.workflow.api.*
import jakarta.inject.Singleton

@Singleton
class BattleActionFrontCreate(
    private val gameEngine: GameEngine,
) {
    internal fun process( data: BattleData, request: WorkflowBattleCreateFrontRequest, player: GameSessionPlayer): BattleData {
        val participant = data.getAndValidateParticipant(player)
        val unit = participant.getAndValidateUnitEntity(request.entityId)

        val index = (data.fronts.lastOrNull()?.index ?: 0) + 1

        val front = createFront(data, participant, unit, index) ?: return data
        data.fronts.add(front)
        data.status = BattleStatus.ONGOING

        val logEntry = BattleLogEntryData(player, BattleActivity.CREATE_FRONT, emptyList())
        data.logEntries.add(logEntry)

        return data
    }

    private fun createFront(data: BattleData, participant: BattleParticipantData, unit: GameEntity, index: Int): BattleFrontData? {
        val availableUnits = participant.units
        if (!availableUnits.any { it == unit }) throw WorkflowBattleUnitNotExisting(unit)

        val existing = data.fronts.find { it.index == index }
        if (existing != null) throw WorkflowBattleInvalidFrontIndex(index)

        val health = gameEngine.getComponent(unit, Health::class) ?: return null
        val data = BattleFrontUnitData(participant.player, unit, health.amount)
        participant.units.remove(unit)
        return BattleFrontData(index, mutableListOf(data))
    }

}