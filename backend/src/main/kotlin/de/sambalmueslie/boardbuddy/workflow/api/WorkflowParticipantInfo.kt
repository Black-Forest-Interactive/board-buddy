package de.sambalmueslie.boardbuddy.workflow.api

import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.engine.api.GameUnit
import de.sambalmueslie.boardbuddy.engine.api.TechnologyType

data class WorkflowParticipantInfo(
    val player: GameSessionPlayer,
    val units: List<GameUnit>,
    val technologies: Set<TechnologyType>
)
