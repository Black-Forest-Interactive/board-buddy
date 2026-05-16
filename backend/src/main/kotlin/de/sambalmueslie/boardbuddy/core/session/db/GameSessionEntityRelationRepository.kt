package de.sambalmueslie.boardbuddy.core.session.db

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.GenericRepository

@JdbcRepository(dialect = Dialect.POSTGRES)
interface GameSessionEntityRelationRepository : GenericRepository<GameSessionEntityRelation, Long> {
    fun save(data: GameSessionEntityRelation): GameSessionEntityRelation
    fun findByGameSessionId(gameSessionId: Long): List<GameSessionEntityRelation>
    fun findByGameSessionIdAndPlayerId(gameSessionId: Long, playerId: Long): List<GameSessionEntityRelation>
    fun findByGameSessionIdAndPlayerIdAndEntityId(gameSessionId: Long, playerId: Long, entityId: Long): GameSessionEntityRelation?
    fun deleteByGameSessionIdAndPlayerIdAndEntityId(gameSessionId: Long, playerId: Long, entityId: Long)
    fun deleteByGameSessionId(gameSessionId: Long)
    fun deleteByPlayerId(playerId: Long)
    fun deleteByEntityId(entityId: Long)

}