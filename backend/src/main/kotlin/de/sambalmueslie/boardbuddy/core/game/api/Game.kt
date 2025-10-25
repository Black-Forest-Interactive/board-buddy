package de.sambalmueslie.boardbuddy.core.game.api

import de.sambalmueslie.boardbuddy.core.common.Entity

data class Game(
    override val id: Long,
    val name: String,
    val description: String
) : Entity
