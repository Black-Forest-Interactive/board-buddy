package de.sambalmueslie.boardbuddy.engine.system

import de.sambalmueslie.boardbuddy.core.nation.api.Nation
import de.sambalmueslie.boardbuddy.core.nation.api.NationEffect
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

    fun create(nation: Nation): GameEntity {
        val entity = model.create(GameEntityType.PLAYER)

        nationModel.create(entity) { NationReference(nation.id) }
        governmentModel.create(entity) { Government(getStartingGovernment(nation)) }

        return entity
    }

    private fun getStartingGovernment(nation: Nation): GovernmentType {
        return nation.effect
            .filterIsInstance<NationEffect.InitialGovernment>()
            .firstOrNull()?.type ?: GovernmentType.DESPOTISM
    }
}