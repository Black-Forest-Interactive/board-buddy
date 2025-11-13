package de.sambalmueslie.boardbuddy.core.unit.db

import de.sambalmueslie.boardbuddy.common.EntityRepository
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect

@Repository
@JdbcRepository(dialect = Dialect.POSTGRES)
interface UnitDefinitionRepository : EntityRepository<UnitDefinitionData> {
}