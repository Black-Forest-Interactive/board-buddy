package de.sambalmueslie.boardbuddy.engine.api

data class GamePlayer(
    val entity: GameEntity,
    val nation: Nation? = null,
    val government: Government? = null,
)
