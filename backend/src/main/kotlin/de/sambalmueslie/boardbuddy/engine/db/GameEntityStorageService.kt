package de.sambalmueslie.boardbuddy.engine.db

import de.sambalmueslie.boardbuddy.common.TimeProvider
import de.sambalmueslie.boardbuddy.common.findByIdOrNull
import de.sambalmueslie.boardbuddy.engine.api.GameEntity
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class GameEntityStorageService(
    private val repository: GameEntityRepository,
    private val componentStorageService: GameComponentStorageService,
    private val timeProvider: TimeProvider
) : GameEntityStorage {


    companion object {
        private val logger = LoggerFactory.getLogger(GameEntityStorageService::class.java)
    }

    override fun create(): GameEntity {
        return repository.save(GameEntityData(0, timeProvider.currentTime())).convert()
    }

    override fun get(id: Long): GameEntity? {
        return repository.findByIdOrNull(id)?.convert()
    }

    override fun delete(entity: GameEntity) {
        componentStorageService.delete(entity)
        repository.deleteById(entity)
    }

}