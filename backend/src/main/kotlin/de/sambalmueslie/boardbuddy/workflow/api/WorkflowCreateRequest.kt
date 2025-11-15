package de.sambalmueslie.boardbuddy.workflow.api

data class WorkflowCreateRequest(
    val name: String,
    val hostId: Long,
    val gameId: Long,
    val ruleSetId: Long
)