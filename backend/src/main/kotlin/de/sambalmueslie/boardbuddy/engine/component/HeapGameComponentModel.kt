package de.sambalmueslie.boardbuddy.engine.component

import de.sambalmueslie.boardbuddy.engine.api.GameComponent
import de.sambalmueslie.boardbuddy.engine.api.GameEntity
import de.sambalmueslie.boardbuddy.engine.storage.GameComponentStorage

class HeapGameComponentModel<T : GameComponent>(
    private val storage: GameComponentStorage<T>
) : GameComponentModel<T> {

    private val data = mutableMapOf<GameEntity, T>()

    override fun create(entity: GameEntity, builder: () -> T): T {
        val component = builder.invoke()
        data[entity] = component
        return component
    }

    override fun add(entity: GameEntity, component: T) {
        data[entity] = component
    }

    override fun get(entity: GameEntity): T? = data[entity]
    override fun all(): Map<GameEntity, T> = data

    internal fun persistAll() {
        data.forEach { (e, v) -> storage.update(e, v) }
    }

    internal fun load(entity: GameEntity): T? {
        val comp = storage.get(entity) ?: return null
        data[entity] = comp
        return comp
    }

    internal fun persist(entity: GameEntity): T? {
        val comp = data[entity] ?: return null
        storage.update(entity, comp)
        return comp
    }

}