package de.sambalmueslie.boardbuddy.core.unit.db

import de.sambalmueslie.boardbuddy.core.common.EntityRepository
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.data.model.query.builder.sql.Dialect

@Repository
@JdbcRepository(dialect = Dialect.POSTGRES)
interface UnitInstanceRepository : EntityRepository<UnitInstanceData> {
    fun getByUnitTypeId(unitTypeId: Long, pageable: Pageable): Page<UnitInstanceData>
    fun deleteByUnitTypeId(id: Long)
}