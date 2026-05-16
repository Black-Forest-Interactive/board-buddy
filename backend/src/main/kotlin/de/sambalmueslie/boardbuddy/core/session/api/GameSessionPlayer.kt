package de.sambalmueslie.boardbuddy.core.session.api

import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.engine.api.GameEntity

data class GameSessionPlayer(
    val player: Player,
    val entity: GameEntity
)
