package de.sambalmueslie.boardbuddy.workflow.api

data class WorkflowBattleAddUnitRequest(
    val playerId: Long,
    val unitInstanceId: Long,
    val index: Int,
)
