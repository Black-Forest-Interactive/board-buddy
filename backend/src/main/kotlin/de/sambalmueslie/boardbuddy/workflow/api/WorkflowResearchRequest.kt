package de.sambalmueslie.boardbuddy.workflow.api

import de.sambalmueslie.boardbuddy.engine.api.TechnologyType

data class WorkflowResearchRequest(
    val technology: TechnologyType,
    val playerId: Long,
)
