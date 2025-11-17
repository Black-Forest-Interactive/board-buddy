package de.sambalmueslie.boardbuddy.engine.storage

import de.sambalmueslie.boardbuddy.engine.api.GameComponent
import de.sambalmueslie.boardbuddy.engine.api.GameEntity

interface GameComponentStorage<T : GameComponent> {
    fun create(entity: GameEntity, builder: () -> T): T
    fun get(entity: GameEntity): T?
    fun update(entity: GameEntity, component: T): T
    fun delete(entity: GameEntity)
}