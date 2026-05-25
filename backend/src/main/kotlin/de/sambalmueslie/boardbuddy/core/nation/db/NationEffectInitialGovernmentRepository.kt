package de.sambalmueslie.boardbuddy.core.nation.db

import de.sambalmueslie.boardbuddy.common.EntityRepository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect

@JdbcRepository(dialect = Dialect.POSTGRES)
interface NationEffectInitialGovernmentRepository : EntityRepository<NationEffectInitialGovernmentData> {
    fun findByNationId(nationId: Long): List<NationEffectInitialGovernmentData>
    fun deleteByNationId(nationId: Long)
}
