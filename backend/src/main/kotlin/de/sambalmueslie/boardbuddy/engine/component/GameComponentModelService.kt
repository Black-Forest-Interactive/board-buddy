package de.sambalmueslie.boardbuddy.engine.component

import de.sambalmueslie.boardbuddy.engine.api.*
import de.sambalmueslie.boardbuddy.engine.storage.GameComponentStorageService
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

@Singleton
class GameComponentModelService(
    private val componentStorageService: GameComponentStorageService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(GameComponentModelService::class.java)
    }

    private val model = mapOf(
        Pair(Damage::class, createModel(Damage::class)),
        Pair(Health::class, createModel(Health::class)),
        Pair(Level::class, createModel(Level::class)),
        Pair(Type::class, createModel(Type::class)),
        Pair(CounterType::class, createModel(CounterType::class)),
    )


    @Suppress("UNCHECKED_CAST")
    fun <T : GameComponent> get(type: KClass<T>): GameComponentModel<T> {
        val entry = model[type] ?: throw IllegalArgumentException("Unknown type: $type")
        return entry as GameComponentModel<T>
    }

    private fun <T : GameComponent> createModel(type: KClass<T>): HeapGameComponentModel<T> {
        return HeapGameComponentModel(componentStorageService.get(type))
    }

    fun persist(entity: GameEntity) {
        model.forEach { (type, model) -> model.persist(entity) }
    }

    fun load(entity: GameEntity) {
        model.forEach { (type, model) -> model.load(entity) }
    }

}