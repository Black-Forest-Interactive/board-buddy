package de.sambalmueslie.boardbuddy.engine.api

data class GameUnit(
    val entity: GameEntity,
    val damage: Damage? = null,
    val health: Health? = null,
    val level: Level? = null,
    val type: Type? = null,
    val counterType: CounterType? = null,
)
