package de.sambalmueslie.boardbuddy.engine.api

interface GameEntity {
    val id: Long
    val components: List<GameComponent>
}