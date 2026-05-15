package de.sambalmueslie.boardbuddy.engine.storage

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import de.sambalmueslie.boardbuddy.common.TimeProvider
import de.sambalmueslie.boardbuddy.common.findByIdOrNull
import de.sambalmueslie.boardbuddy.engine.api.GameEntity
import de.sambalmueslie.boardbuddy.engine.api.GameEntityType
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

@Singleton
class GameEntityStorageService(
    private val repository: GameEntityRepository,
    private val componentStorageService: GameComponentStorageService,
    private val timeProvider: TimeProvider
) : GameEntityStorage {


    companion object {
        private val logger = LoggerFactory.getLogger(GameEntityStorageService::class.java)
    }

    private val cache: LoadingCache<Long, GameEntityData?> = Caffeine.newBuilder()
        .maximumSize(100)
        .expireAfterWrite(1, TimeUnit.HOURS)
        .recordStats()
        .build { repository.findByIdOrNull(it) }

    override fun create(type: GameEntityType): GameEntity {
        val data = repository.save(GameEntityData(0, type, timeProvider.currentTime()))
        cache.put(data.id, data)
        return data.convert()
    }

    override fun get(id: Long): GameEntity? {
        return cache.get(id)?.convert()
    }

    override fun get(id: Long, type: GameEntityType): GameEntity? {
        val data = cache[id] ?: return null
        if (data.type != type) return null
        return data.convert()
    }

    override fun delete(entity: GameEntity) {
        componentStorageService.delete(entity)
        repository.deleteById(entity)
        cache.invalidate(entity)

    }

    override fun exists(entity: GameEntity): Boolean {
        return cache.get(entity) != null
    }

}