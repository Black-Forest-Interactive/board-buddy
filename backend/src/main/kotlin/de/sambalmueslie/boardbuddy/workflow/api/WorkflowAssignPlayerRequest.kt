package de.sambalmueslie.boardbuddy.workflow.api

import de.sambalmueslie.boardbuddy.engine.api.NationType

data class WorkflowAssignPlayerRequest(
    val playerId: Long,
    val nation: NationType,
)
