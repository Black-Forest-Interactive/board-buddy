package de.sambalmueslie.boardbuddy.engine

import de.sambalmueslie.boardbuddy.engine.api.*
import de.sambalmueslie.boardbuddy.engine.common.ComponentStore
import jakarta.inject.Singleton
import kotlin.reflect.KClass

@Singleton
class ComponentStoreService {
    private val damageStore = ComponentStore<Damage>()
    private val healthStore = ComponentStore<Health>()
    private val levelStore = ComponentStore<Level>()
    private val typeStore = ComponentStore<Type>()
    private val counterTypeStore = ComponentStore<CounterType>()

    @Suppress("UNCHECKED_CAST")
    fun <T : GameComponent> get(type: KClass<T>): ComponentStore<T> {
        val store = when (type) {
            Damage::class -> damageStore
            Health::class -> healthStore
            Level::class -> levelStore
            Type::class -> typeStore
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
        return store as ComponentStore<T>
    }

}