package de.sambalmueslie.boardbuddy.core.session.db

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.GenericRepository

@Repository
@JdbcRepository(dialect = Dialect.POSTGRES)
interface GameSessionUnitRelationRepository : GenericRepository<GameSessionUnitRelation, Long> {
    fun save(data: GameSessionUnitRelation): GameSessionUnitRelation
    fun findByGameSessionId(gameSessionId: Long): List<GameSessionUnitRelation>
    fun findByGameSessionIdAndPlayerId(gameSessionId: Long, playerId: Long): List<GameSessionUnitRelation>
    fun findByGameSessionIdAndPlayerIdAndUnitInstanceId(gameSessionId: Long, playerId: Long, unitInstanceId: Long): GameSessionUnitRelation?
    fun deleteByGameSessionIdAndPlayerIdAndUnitInstanceId(gameSessionId: Long, playerId: Long, unitInstanceId: Long)
    fun deleteByGameSessionId(gameSessionId: Long)
    fun deleteByPlayerId(playerId: Long)
    fun deleteByUnitInstanceId(unitInstanceId: Long)

}