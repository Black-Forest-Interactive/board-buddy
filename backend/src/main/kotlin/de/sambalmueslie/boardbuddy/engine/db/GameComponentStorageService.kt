package de.sambalmueslie.boardbuddy.engine.db

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
) : GameComponentStorage {


    companion object {
        private val logger = LoggerFactory.getLogger(GameComponentStorageService::class.java)
    }

    private val counterTypeStore = ComponentStorage(counterTypeRepository) { e, t -> ComponentCounterTypeData(e, t.kind, timeProvider.currentTime()) }
    private val damageStore = ComponentStorage(damageRepository) { e, t -> ComponentDamageData(e, t.amount, timeProvider.currentTime()) }
    private val healthStore = ComponentStorage(healthRepository) { e, t -> ComponentHealthData(e, t.amount, timeProvider.currentTime()) }
    private val levelStore = ComponentStorage(levelRepository) { e, t -> ComponentLevelData(e, t.value, timeProvider.currentTime()) }
    private val typeStore = ComponentStorage(typeRepository) { e, t -> ComponentTypeData(e, t.kind, timeProvider.currentTime()) }

    override fun <T : GameComponent> create(entity: GameEntity, type: KClass<T>, builder: () -> T): T {
        return getStorage(type).create(entity, builder)
    }

    override fun <T : GameComponent> get(entity: GameEntity, type: KClass<T>): T? {
        return getStorage(type).get(entity)
    }

    override fun <T : GameComponent> update(entity: GameEntity, type: KClass<T>, component: T): T {
        return getStorage(type).update(entity, component)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : GameComponent, D : GameComponentData<T, D>> getStorage(type: KClass<T>): ComponentStorage<T, D> {
        val storage = when (type) {
            Damage::class -> damageStore
            Health::class -> healthStore
            Level::class -> levelStore
            Type::class -> typeStore
            CounterType::class -> counterTypeStore
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
        return storage as ComponentStorage<T, D>
    }

    fun delete(entity: GameEntity) {
        counterTypeStore.delete(entity)
        damageStore.delete(entity)
        healthStore.delete(entity)
        levelStore.delete(entity)
        typeStore.delete(entity)
    }


}