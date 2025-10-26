package de.sambalmueslie.boardbuddy.core.ruleset.db

import de.sambalmueslie.boardbuddy.core.common.EntityRepository
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect

@Repository
@JdbcRepository(dialect = Dialect.POSTGRES)
interface RuleSetRepository : EntityRepository<RuleSetData> {
}