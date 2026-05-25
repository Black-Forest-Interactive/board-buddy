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
    governmentRepository: ComponentGovernmentRepository,
    nationRepository: ComponentNationRepository,
    technologyRepository: ComponentTechnologyRepository,
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
    private val governmentStore = GameComponentStorageOperator(governmentRepository) { e, t -> ComponentGovernmentData(e, t.type, timeProvider.currentTime()) }
    private val nationStore = GameComponentStorageOperator(nationRepository) { e, t -> ComponentNationData(e, t.type, timeProvider.currentTime()) }
    private val technologiesStore = GameComponentStorageOperator(technologyRepository) { e, t ->
        ComponentTechnologyData(e, t.ids.map { it.toString() }, timeProvider.currentTime())
    }

    private val operator = mapOf(
        CounterType::class to counterTypeStore,
        Damage::class to damageStore,
        Health::class to healthStore,
        Level::class to levelStore,
        Type::class to typeStore,
        Government::class to governmentStore,
        Nation::class to nationStore,
        Technologies::class to technologiesStore,
    )

    @Suppress("UNCHECKED_CAST")
    fun <T : GameComponent> get(type: KClass<T>): GameComponentStorage<T> {
        return operator[type] as? GameComponentStorage<T> ?: throw IllegalArgumentException("Unknown type: $type")
    }

    internal fun delete(entity: GameEntity) {
        operator.values.forEach { it.delete(entity) }
    }
}
