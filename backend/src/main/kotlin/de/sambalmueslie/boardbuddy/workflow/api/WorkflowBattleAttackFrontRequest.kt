package de.sambalmueslie.boardbuddy.workflow.api

data class WorkflowBattleAttackFrontRequest(
    val attackerId: Long,
    val defenderId: Long,
    val entityId: Long,
    val frontIndex: Int,
)
