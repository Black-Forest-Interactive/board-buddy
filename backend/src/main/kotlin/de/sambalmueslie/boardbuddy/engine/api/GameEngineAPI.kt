package de.sambalmueslie.boardbuddy.engine.api

import kotlin.reflect.KClass

interface GameEngineAPI : GameEngineActionAPI, GameEngineDecisionAPI {
    fun <T : GameComponent> getComponent(entity: GameEntity, type: KClass<T>): T?
    fun exists(entity: GameEntity): Boolean
    fun getUnit(entity: GameEntity): GameUnit
    fun getPlayer(entity: GameEntity): GamePlayer
    fun delete(entity: GameEntity)
}