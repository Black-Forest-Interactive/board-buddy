package de.sambalmueslie.boardbuddy.engine.db

import de.sambalmueslie.boardbuddy.engine.api.GameEntity

interface GameEntityStorage {
    fun create(): GameEntity
    fun get(id: Long): GameEntity?
    fun delete(entity: GameEntity)
}