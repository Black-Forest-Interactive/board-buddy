package de.sambalmueslie.boardbuddy.engine.system

import de.sambalmueslie.boardbuddy.core.nation.api.Nation
import de.sambalmueslie.boardbuddy.core.nation.api.NationEffect
import de.sambalmueslie.boardbuddy.core.unit.api.UnitDefinition
import de.sambalmueslie.boardbuddy.engine.api.*
import de.sambalmueslie.boardbuddy.engine.component.GameComponentModelService
import de.sambalmueslie.boardbuddy.engine.storage.GameEntityStorage
import jakarta.inject.Singleton

@Singleton
class CreatePlayerSystem(
    private val model: GameEntityStorage,
    componentModelService: GameComponentModelService,
) : GameSystem {
    private val nationModel = componentModelService.get(NationReference::class)
    private val governmentModel = componentModelService.get(Government::class)
    private val unitProgressModel = componentModelService.get(UnitProgress::class)

    private val initialUnitTypes = setOf(UnitType.INFANTRY, UnitType.MOUNTED, UnitType.ARTILLERY)

    fun create(nation: Nation, unitDefinitions: List<UnitDefinition>): GameEntity {
        val entity = model.create(GameEntityType.PLAYER)

        nationModel.create(entity) { NationReference(nation.id) }
        governmentModel.create(entity) { Government(getStartingGovernment(nation)) }
        val initialEntries = unitDefinitions.filter { initialUnitTypes.contains(it.unitType) }.associate { it.unitType to it.toUnitProgressEntry() }
        unitProgressModel.create(entity) { UnitProgress(initialEntries) }

        return entity
    }

    private fun UnitDefinition.toUnitProgressEntry(): UnitProgressEntry {
        return UnitProgressEntry(1, damagePoints.min, damagePoints.max, healthPoints.min, healthPoints.max)
    }

    private fun getStartingGovernment(nation: Nation): GovernmentType {
        return nation.effect
            .filterIsInstance<NationEffect.InitialGovernment>()
            .firstOrNull()?.type ?: GovernmentType.DESPOTISM
    }
}