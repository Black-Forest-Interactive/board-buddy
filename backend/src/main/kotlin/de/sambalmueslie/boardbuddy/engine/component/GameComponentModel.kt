package de.sambalmueslie.boardbuddy.engine.component

import de.sambalmueslie.boardbuddy.engine.api.GameComponent
import de.sambalmueslie.boardbuddy.engine.api.GameEntity

interface GameComponentModel<T : GameComponent> {
    fun create(entity: GameEntity, builder: () -> T): T
    fun add(entity: GameEntity, component: T)
    fun get(entity: GameEntity): T?
    fun all(): Map<GameEntity, T>
}