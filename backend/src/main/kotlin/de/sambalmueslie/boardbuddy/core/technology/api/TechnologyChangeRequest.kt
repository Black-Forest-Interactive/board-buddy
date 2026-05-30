package de.sambalmueslie.boardbuddy.core.technology.api

import de.sambalmueslie.boardbuddy.common.EntityChangeRequest

data class TechnologyChangeRequest(
    val name: String,
    val description: String,
    val imageUrl: String,
    val tier: Int,
): EntityChangeRequest
