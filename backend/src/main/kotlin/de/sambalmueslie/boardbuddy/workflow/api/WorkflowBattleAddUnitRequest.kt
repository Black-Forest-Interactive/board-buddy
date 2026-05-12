package de.sambalmueslie.boardbuddy.workflow.api

data class WorkflowBattleAddUnitRequest(
    val playerId: Long,
    val entityId: Long,
    val index: Int,
)
