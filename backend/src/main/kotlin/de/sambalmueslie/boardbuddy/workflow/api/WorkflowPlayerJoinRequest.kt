package de.sambalmueslie.boardbuddy.workflow.api

import de.sambalmueslie.boardbuddy.engine.api.NationType

data class WorkflowPlayerJoinRequest(
    val name: String,
    val nation: NationType
)
