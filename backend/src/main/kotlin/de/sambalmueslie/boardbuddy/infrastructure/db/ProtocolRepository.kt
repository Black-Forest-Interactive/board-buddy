package de.sambalmueslie.boardbuddy.infrastructure.db

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository

@JdbcRepository(dialect = Dialect.POSTGRES)
interface ProtocolRepository : CrudRepository<ProtocolData, Long> {
    fun findByAppAndResource(app: String, resource: String): ProtocolData?
}