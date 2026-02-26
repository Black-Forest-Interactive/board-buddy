package de.sambalmueslie.boardbuddy.core.game.api

import de.sambalmueslie.boardbuddy.common.Entity
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSet

data class Game(
    override val id: Long,
    val name: String,
    val description: String,
    val ruleSets: List<RuleSet>,
) : Entity
