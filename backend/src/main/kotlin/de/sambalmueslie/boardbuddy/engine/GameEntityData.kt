package de.sambalmueslie.boardbuddy.engine

import de.sambalmueslie.boardbuddy.common.Entity
import de.sambalmueslie.boardbuddy.engine.api.GameComponent
import de.sambalmueslie.boardbuddy.engine.api.GameEntity

data class GameEntityData(
    override val id: Long,
) : GameEntity, Entity {
    override val components: MutableList<GameComponent> = mutableListOf()
    fun add(component: GameComponent) {
        components.add(component)
    }
}
