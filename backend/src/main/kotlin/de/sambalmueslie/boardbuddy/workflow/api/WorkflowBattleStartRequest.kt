package de.sambalmueslie.boardbuddy.workflow.api

data class WorkflowBattleStartRequest(
    val attackerId: Long,
    val defenderId: Long,
)
