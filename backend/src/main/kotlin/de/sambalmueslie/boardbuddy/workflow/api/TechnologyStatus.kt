package de.sambalmueslie.boardbuddy.workflow.api

import de.sambalmueslie.boardbuddy.core.technology.api.Technology

data class TechnologyStatus(
    val researched: List<Technology>,
    val available: List<Technology>,
    val blocked: List<Technology>,
)