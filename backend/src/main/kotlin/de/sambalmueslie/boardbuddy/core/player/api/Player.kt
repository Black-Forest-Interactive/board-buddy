package de.sambalmueslie.boardbuddy.core.player.api

import de.sambalmueslie.boardbuddy.common.Entity

data class Player(
    override val id: Long,
    val name: String
) : Entity
