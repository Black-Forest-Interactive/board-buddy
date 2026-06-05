package de.sambalmueslie.boardbuddy.core.player.db

import de.sambalmueslie.boardbuddy.common.EntityRepository
import de.sambalmueslie.boardbuddy.core.player.api.PlayerType
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect

@JdbcRepository(dialect = Dialect.POSTGRES)
interface PlayerRepository : EntityRepository<PlayerData> {
    fun findByType(type: PlayerType): List<PlayerData>
}