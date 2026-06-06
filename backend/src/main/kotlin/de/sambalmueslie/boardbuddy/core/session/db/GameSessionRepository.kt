package de.sambalmueslie.boardbuddy.core.session.db

import de.sambalmueslie.boardbuddy.common.EntityRepository
import io.micronaut.data.annotation.Query
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import java.time.LocalDateTime

@JdbcRepository(dialect = Dialect.POSTGRES)
interface GameSessionRepository : EntityRepository<GameSessionData> {
    fun findByKey(key: String): GameSessionData?

    @Query("SELECT * FROM game_session WHERE COALESCE(updated, created) < :cutoff")
    fun findInactiveSince(cutoff: LocalDateTime): List<GameSessionData>
}