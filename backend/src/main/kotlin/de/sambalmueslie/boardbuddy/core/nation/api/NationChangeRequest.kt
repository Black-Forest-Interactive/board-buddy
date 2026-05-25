package de.sambalmueslie.boardbuddy.core.nation.api

import de.sambalmueslie.boardbuddy.common.EntityChangeRequest

data class NationChangeRequest(
    val name: String,
    val description: String,
    val imageUrl: String,
): EntityChangeRequest
