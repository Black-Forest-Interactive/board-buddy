package de.sambalmueslie.boardbuddy.core.ruleset.db

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.GenericRepository

@JdbcRepository(dialect = Dialect.POSTGRES)
interface RuleSetNationRelationRepository : GenericRepository<RuleSetNationRelation, Long> {
    fun save(data: RuleSetNationRelation): RuleSetNationRelation
    fun findByRuleSetId(ruleSetId: Long): List<RuleSetNationRelation>
    fun findByRuleSetIdAndNationId(ruleSetId: Long, nationId: Long): RuleSetNationRelation?
    fun deleteByRuleSetIdAndNationId(ruleSetId: Long, nationId: Long)
    fun deleteByRuleSetId(ruleSetId: Long)
    fun deleteByNationId(nationId: Long)
}