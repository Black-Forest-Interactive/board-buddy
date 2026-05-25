package de.sambalmueslie.boardbuddy.core.ruleset.api

import de.sambalmueslie.boardbuddy.common.Entity
import de.sambalmueslie.boardbuddy.core.nation.api.Nation
import de.sambalmueslie.boardbuddy.core.technology.api.Technology
import de.sambalmueslie.boardbuddy.core.unit.api.UnitDefinition

data class RuleSet(
    override val id: Long,
    val name: String,
    val unitDefinitions: List<UnitDefinition>,
    val technologies: List<Technology>,
    val nations: List<Nation>,
) : Entity
