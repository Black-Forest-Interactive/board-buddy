package de.sambalmueslie.boardbuddy.engine.storage

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect

@JdbcRepository(dialect = Dialect.POSTGRES)
interface ComponentUnitProgressRepository : GameComponentRepository<ComponentUnitProgressData>
