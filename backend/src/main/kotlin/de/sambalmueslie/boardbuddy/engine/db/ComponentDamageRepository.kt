package de.sambalmueslie.boardbuddy.engine.db

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect

@Repository
@JdbcRepository(dialect = Dialect.POSTGRES)
interface ComponentDamageRepository : GameComponentRepository<ComponentDamageData> {

}