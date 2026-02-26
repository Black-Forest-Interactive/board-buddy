package de.sambalmueslie.boardbuddy.core.player.api

import de.sambalmueslie.boardbuddy.common.EntityChangeRequest

data class PlayerChangeRequest(
    val name: String
) : EntityChangeRequest