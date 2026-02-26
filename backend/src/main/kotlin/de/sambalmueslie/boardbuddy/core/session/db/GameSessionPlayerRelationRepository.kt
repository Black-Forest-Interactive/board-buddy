package de.sambalmueslie.boardbuddy.core.session.db

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.GenericRepository

@Repository
@JdbcRepository(dialect = Dialect.POSTGRES)
interface GameSessionPlayerRelationRepository : GenericRepository<GameSessionPlayerRelation, Long> {
    fun save(data: GameSessionPlayerRelation): GameSessionPlayerRelation
    fun findByGameSessionId(gameSessionId: Long): List<GameSessionPlayerRelation>
    fun findByGameSessionIdAndPlayerId(gameSessionId: Long, playerId: Long): GameSessionPlayerRelation?
    fun deleteByGameSessionIdAndPlayerId(gameSessionId: Long, playerId: Long)
    fun deleteByGameSessionId(gameSessionId: Long)
    fun deleteByPlayerId(playerId: Long)

}