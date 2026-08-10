package de.sambalmueslie.boardbuddy.infrastructure.db

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import java.time.LocalDateTime

@JdbcRepository(dialect = Dialect.POSTGRES)
interface ProtocolEntryRepository : CrudRepository<ProtocolEntryData, Long> {

    fun deleteByTimestampBefore(timestamp: LocalDateTime)

}