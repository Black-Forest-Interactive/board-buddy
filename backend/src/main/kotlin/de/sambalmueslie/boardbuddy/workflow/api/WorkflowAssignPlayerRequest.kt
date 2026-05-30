package de.sambalmueslie.boardbuddy.workflow.api

data class WorkflowAssignPlayerRequest(
    val playerId: Long,
    val nationId: Long,
)
