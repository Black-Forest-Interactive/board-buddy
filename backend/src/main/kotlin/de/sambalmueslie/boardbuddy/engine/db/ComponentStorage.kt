package de.sambalmueslie.boardbuddy.engine.db

import de.sambalmueslie.boardbuddy.common.findByIdOrNull
import de.sambalmueslie.boardbuddy.engine.api.GameComponent
import de.sambalmueslie.boardbuddy.engine.api.GameEntity

internal class ComponentStorage<T : GameComponent, D : GameComponentData<T, D>>(
    private val repository: GameComponentRepository<D>,
    private val mapper: (GameEntity, T) -> D
) {
    fun create(entity: GameEntity, builder: () -> T): T {
        val data = builder.invoke().let { mapper.invoke(entity, it) }
        return modify(entity, data)
    }

    fun get(entity: GameEntity): T? {
        return repository.findByIdOrNull(entity)?.convert()
    }

    fun update(entity: GameEntity, component: T): T {
        val data = mapper.invoke(entity, component)
        return modify(entity, data)
    }

    fun delete(entity: GameEntity) {
        repository.deleteById(entity)
    }

    private fun modify(entity: GameEntity, data: D): T {
        val existing = repository.findByIdOrNull(entity)
        return if (existing != null) {
            repository.update(existing.update(data))
        } else {
            repository.save(data)
        }.convert()
    }
}