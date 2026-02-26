package de.sambalmueslie.boardbuddy.core.game.db

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.GenericRepository

@Repository
@JdbcRepository(dialect = Dialect.POSTGRES)
interface GameRuleSetRelationRepository : GenericRepository<GameRuleSetRelation, Long> {
    fun save(data: GameRuleSetRelation): GameRuleSetRelation
    fun findByGameId(gameId: Long): List<GameRuleSetRelation>
    fun findByGameIdAndRuleSetId(gameId: Long, ruleSetId: Long): GameRuleSetRelation?
    fun deleteByGameIdAndRuleSetId(gameId: Long, ruleSetId: Long)
    fun deleteByGameId(gameId: Long)
    fun deleteByRuleSetId(ruleSetId: Long)
}