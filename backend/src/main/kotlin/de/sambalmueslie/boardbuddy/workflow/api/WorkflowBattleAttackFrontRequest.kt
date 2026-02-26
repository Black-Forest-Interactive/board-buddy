package de.sambalmueslie.boardbuddy.workflow.api

data class WorkflowBattleAttackFrontRequest(
    val attackerId: Long,
    val defenderId: Long,
    val unitInstanceId: Long,
    val frontIndex: Int,
)
