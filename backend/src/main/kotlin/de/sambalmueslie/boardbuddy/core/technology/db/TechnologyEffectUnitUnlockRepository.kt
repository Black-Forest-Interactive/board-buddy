package de.sambalmueslie.boardbuddy.core.technology.db

import de.sambalmueslie.boardbuddy.common.EntityRepository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect

@JdbcRepository(dialect = Dialect.POSTGRES)
interface TechnologyEffectUnitUnlockRepository : EntityRepository<TechnologyEffectUnitUnlockData> {
    fun findByTechnologyId(technologyId: Long): List<TechnologyEffectUnitUnlockData>
    fun deleteByTechnologyId(technologyId: Long)
}
