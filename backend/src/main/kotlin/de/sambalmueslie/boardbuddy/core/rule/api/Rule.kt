package de.sambalmueslie.boardbuddy.core.rule.api

import de.sambalmueslie.boardbuddy.common.Entity

data class Rule(
    override val id: Long,
    val name: String,
    val description: String,
    val type: String,
    val properties: Map<String, String>,
) : Entity
