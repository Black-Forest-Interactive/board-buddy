package de.sambalmueslie.boardbuddy.core.workflow.api

data class WorkflowBattleAttackFrontRequest(
    val attackerId: Long,
    val defenderId: Long,
    val unitInstanceId: Long,
    val frontIndex: Int,
)
