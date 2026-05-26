package de.sambalmueslie.boardbuddy.workflow.api

import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.core.technology.api.Technology
import de.sambalmueslie.boardbuddy.engine.api.GameUnit
import de.sambalmueslie.boardbuddy.engine.api.Government
import de.sambalmueslie.boardbuddy.engine.api.NationReference
import de.sambalmueslie.boardbuddy.engine.api.UnitType

data class WorkflowParticipantInfo(
    val player: Player,
    val nation: NationReference?,
    val government: Government?,
    val units: List<GameUnit>,
    val unitLevel: Map<UnitType, Int>,
    val technologies: List<Technology>,
    val availableTechnologies: List<Technology>
)
