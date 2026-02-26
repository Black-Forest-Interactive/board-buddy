package de.sambalmueslie.boardbuddy.engine.model

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import de.sambalmueslie.boardbuddy.engine.api.GameEntity
import de.sambalmueslie.boardbuddy.engine.storage.GameEntityStorage
import jakarta.inject.Singleton
import java.util.concurrent.TimeUnit

@Singleton
class GameEntityModel(private val storage: GameEntityStorage) {

    private val cache: Cache<Long, GameEntity> = Caffeine.newBuilder()
        .maximumSize(100)
        .expireAfterWrite(1, TimeUnit.HOURS)
        .recordStats()
        .build()

    fun create(): GameEntity {
        val data = storage.create()
        cache.put(data, data)
        return data
    }

    fun get(entityId: Long): GameEntity? {
        val cached = cache.getIfPresent(entityId)
        if (cached != null) return cached

        val game = storage.get(entityId) ?: return null
        cache.put(entityId, game)
        return game
    }

}