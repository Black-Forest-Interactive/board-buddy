package de.sambalmueslie.boardbuddy.engine.system

import de.sambalmueslie.boardbuddy.engine.api.*
import de.sambalmueslie.boardbuddy.engine.component.GameComponentModelService
import de.sambalmueslie.boardbuddy.engine.model.GameEntityModel
import jakarta.inject.Singleton

@Singleton
class CreatePlayerSystem(
    private val model: GameEntityModel,
    componentModelService: GameComponentModelService,
) : GameSystem {
    private val nationModel = componentModelService.get(Nation::class)
    private val governmentModel = componentModelService.get(Government::class)

    fun create(nation: NationType): GameEntity {
        val entity = model.create(GameEntityType.PLAYER)

        val nation = nationModel.create(entity) { Nation(nation) }
        governmentModel.create(entity) { Government(getStartingGovernment(nation)) }

        return entity
    }

    private fun getStartingGovernment(nation: Nation): GovernmentType {
        return when (nation.type) {
            NationType.ROME -> GovernmentType.REPUBLIC
            NationType.RUSSIA -> GovernmentType.COMMUNISM
            NationType.JAPANESE -> GovernmentType.FUNDAMENTALISM
            else -> GovernmentType.DESPOTISM
        }
    }
}