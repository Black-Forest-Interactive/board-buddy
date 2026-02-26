package de.sambalmueslie.boardbuddy.core.ruleset.api

import de.sambalmueslie.boardbuddy.common.Entity
import de.sambalmueslie.boardbuddy.core.unit.api.UnitDefinition

data class RuleSet(
    override val id: Long,
    val name: String,
    val unitDefinitions: List<UnitDefinition>
) : Entity
