package de.sambalmueslie.boardbuddy.engine.db

import de.sambalmueslie.boardbuddy.engine.api.GameComponent
import de.sambalmueslie.boardbuddy.engine.api.GameEntity
import kotlin.reflect.KClass

interface GameComponentStorage {
    fun <T : GameComponent> create(entity: GameEntity, type: KClass<T>, builder: () -> T): T
    fun <T : GameComponent> get(entity: GameEntity, type: KClass<T>): T?
    fun <T : GameComponent> update(entity: GameEntity, type: KClass<T>, component: T): T
}