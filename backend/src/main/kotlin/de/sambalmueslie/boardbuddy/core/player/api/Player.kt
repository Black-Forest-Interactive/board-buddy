package de.sambalmueslie.boardbuddy.core.player.api

import de.sambalmueslie.boardbuddy.common.Entity
import java.time.LocalDateTime

data class Player(
    override val id: Long,
    val type: PlayerType,
    val name: String,
    val timestamp: LocalDateTime
) : Entity
