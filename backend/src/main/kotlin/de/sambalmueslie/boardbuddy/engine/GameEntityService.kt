package de.sambalmueslie.boardbuddy.engine

import de.sambalmueslie.boardbuddy.engine.api.GameEntity
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class GameEntityService {
    companion object {
        private val logger = LoggerFactory.getLogger(GameEntityService::class.java)
    }


    private val entities = mutableMapOf<Long, GameEntity>()

    fun createEntity(): GameEntityData {
        val data = GameEntityData(entities.size + 1L)
        entities[data.id] = data
        return data
    }



}