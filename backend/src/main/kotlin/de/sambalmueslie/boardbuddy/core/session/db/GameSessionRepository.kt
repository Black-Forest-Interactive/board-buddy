package de.sambalmueslie.boardbuddy.core.session.db

import de.sambalmueslie.boardbuddy.core.common.EntityRepository
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect

@Repository
@JdbcRepository(dialect = Dialect.POSTGRES)
interface GameSessionRepository : EntityRepository<GameSessionData> {
}