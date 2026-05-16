package de.sambalmueslie.boardbuddy.workflow.battle

import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.engine.api.GameEntity
import de.sambalmueslie.boardbuddy.workflow.api.WorkflowBattleUnitNotExisting

data class BattleParticipantData(
    val player: GameSessionPlayer,
    val armyCount: Int,
    val units: MutableList<GameEntity>,
) {
    fun matches(p: GameSessionPlayer) = player.player.id == p.player.id

    fun getAndValidateUnitEntity(entityId: Long): GameEntity {
        return units.find { it == entityId } ?: throw WorkflowBattleUnitNotExisting(entityId)
    }
}