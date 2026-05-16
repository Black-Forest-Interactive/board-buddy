package de.sambalmueslie.boardbuddy.core.player.db

import de.sambalmueslie.boardbuddy.common.EntityRepository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect

@JdbcRepository(dialect = Dialect.POSTGRES)
interface PlayerRepository : EntityRepository<PlayerData> {
}