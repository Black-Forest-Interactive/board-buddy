package de.sambalmueslie.boardbuddy.core.ruleset.api

import de.sambalmueslie.boardbuddy.common.EntityChangeRequest

data class RuleSetChangeRequest(
    val name: String,
) : EntityChangeRequest
