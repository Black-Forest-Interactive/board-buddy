package de.sambalmueslie.boardbuddy.core.game.api

import de.sambalmueslie.boardbuddy.common.EntityChangeRequest

data class GameChangeRequest(
    val name: String,
    val description: String,
) : EntityChangeRequest
