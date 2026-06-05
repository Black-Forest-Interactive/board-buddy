package de.sambalmueslie.boardbuddy.workflow.battle.api

data class WorkflowBattleAttackFrontRequest(
    val attackerId: Long,
    val defenderId: Long,
    val entityId: Long,
    val frontIndex: Int,
)
