package de.sambalmueslie.boardbuddy.engine.api

data class GameEntityInfo(
    val entity: GameEntity,
    val components: List<GameComponent>
)
