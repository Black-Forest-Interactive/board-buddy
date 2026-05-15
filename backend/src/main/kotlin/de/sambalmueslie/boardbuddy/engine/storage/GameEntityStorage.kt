package de.sambalmueslie.boardbuddy.engine.storage

import de.sambalmueslie.boardbuddy.engine.api.GameEntity
import de.sambalmueslie.boardbuddy.engine.api.GameEntityType

interface GameEntityStorage {
    fun create(type: GameEntityType): GameEntity
    fun get(id: Long): GameEntity?
    fun get(id: Long, type: GameEntityType): GameEntity?
    fun delete(entity: GameEntity)
    fun exists(entity: GameEntity): Boolean
}