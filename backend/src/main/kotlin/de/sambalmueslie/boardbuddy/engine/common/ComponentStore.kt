package de.sambalmueslie.boardbuddy.engine.common

import de.sambalmueslie.boardbuddy.engine.GameEntityData
import de.sambalmueslie.boardbuddy.engine.api.GameComponent

class ComponentStore<T : GameComponent> {
    private val data = mutableMapOf<GameEntityData, T>()

    fun create(entity: GameEntityData, builder: () -> T): T {
        val component = builder.invoke()
        data[entity] = component
        entity.add(component)
        return component
    }

    fun add(entity: GameEntityData, component: T) {
        data[entity] = component
    }

    fun get(entity: GameEntityData): T? = data[entity]
    fun all(): Map<GameEntityData, T> = data
}