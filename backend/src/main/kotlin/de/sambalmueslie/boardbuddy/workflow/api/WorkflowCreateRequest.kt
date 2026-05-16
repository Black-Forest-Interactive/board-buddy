package de.sambalmueslie.boardbuddy.workflow.api

import de.sambalmueslie.boardbuddy.engine.api.NationType

data class WorkflowCreateRequest(
    val name: String,
    val hostId: Long,
    val gameId: Long,
    val ruleSetId: Long,
    val nation: NationType
)