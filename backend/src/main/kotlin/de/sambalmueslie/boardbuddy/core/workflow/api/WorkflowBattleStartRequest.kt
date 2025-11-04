package de.sambalmueslie.boardbuddy.core.workflow.api

data class WorkflowBattleStartRequest(
    val attackerId: Long,
    val defenderId: Long,
)
