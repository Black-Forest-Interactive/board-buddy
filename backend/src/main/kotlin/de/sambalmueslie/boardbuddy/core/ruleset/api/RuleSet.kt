package de.sambalmueslie.boardbuddy.core.ruleset.api

import de.sambalmueslie.boardbuddy.core.common.Entity

data class RuleSet(
    override val id: Long,
    val name: String,
//    val unitTypes: List<UnitType>
) : Entity
