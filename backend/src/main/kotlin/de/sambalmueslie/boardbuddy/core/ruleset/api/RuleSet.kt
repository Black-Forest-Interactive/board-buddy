package de.sambalmueslie.boardbuddy.core.ruleset.api

import de.sambalmueslie.boardbuddy.core.common.Entity
import de.sambalmueslie.boardbuddy.core.unit.api.UnitType

data class RuleSet(
    override val id: Long,
    val name: String,
    val unitTypes: List<UnitType>
) : Entity
