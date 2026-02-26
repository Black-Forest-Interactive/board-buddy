package de.sambalmueslie.boardbuddy.engine.storage

import de.sambalmueslie.boardbuddy.common.TimeProvider
import de.sambalmueslie.boardbuddy.engine.api.*
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

@Singleton
class GameComponentStorageService(
    counterTypeRepository: ComponentCounterTypeRepository,
    damageRepository: ComponentDamageRepository,
    healthRepository: ComponentHealthRepository,
    levelRepository: ComponentLevelRepository,
    typeRepository: ComponentTypeRepository,
    private val timeProvider: TimeProvider
) {

    companion object {
        private val logger = LoggerFactory.getLogger(GameComponentStorageService::class.java)
    }

    private val counterTypeStore = GameComponentStorageOperator(counterTypeRepository) { e, t -> ComponentCounterTypeData(e, t.kind, timeProvider.currentTime()) }
    private val damageStore = GameComponentStorageOperator(damageRepository) { e, t -> ComponentDamageData(e, t.amount, timeProvider.currentTime()) }
    private val healthStore = GameComponentStorageOperator(healthRepository) { e, t -> ComponentHealthData(e, t.amount, timeProvider.currentTime()) }
    private val levelStore = GameComponentStorageOperator(levelRepository) { e, t -> ComponentLevelData(e, t.value, timeProvider.currentTime()) }
    private val typeStore = GameComponentStorageOperator(typeRepository) { e, t -> ComponentTypeData(e, t.kind, timeProvider.currentTime()) }


    @Suppress("UNCHECKED_CAST")
    fun <T : GameComponent> get(type: KClass<T>): GameComponentStorage<T> {
        val storage = when (type) {
            Damage::class -> damageStore
            Health::class -> healthStore
            Level::class -> levelStore
            Type::class -> typeStore
            CounterType::class -> counterTypeStore
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
        return storage as GameComponentStorage<T>
    }

    internal fun delete(entity: GameEntity) {
        counterTypeStore.delete(entity)
        damageStore.delete(entity)
        healthStore.delete(entity)
        levelStore.delete(entity)
        typeStore.delete(entity)
    }
}