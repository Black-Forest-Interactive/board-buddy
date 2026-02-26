package de.sambalmueslie.boardbuddy.core.ruleset.db

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.GenericRepository

@Repository
@JdbcRepository(dialect = Dialect.POSTGRES)
interface RuleSetUnitTypeRelationRepository : GenericRepository<RuleSetUnitTypeRelation, Long> {
    fun save(data: RuleSetUnitTypeRelation): RuleSetUnitTypeRelation
    fun findByRuleSetId(ruleSetId: Long): List<RuleSetUnitTypeRelation>
    fun findByRuleSetIdAndUnitTypeId(ruleSetId: Long, unitTypeId: Long): RuleSetUnitTypeRelation?
    fun deleteByRuleSetIdAndUnitTypeId(ruleSetId: Long, unitTypeId: Long)
    fun deleteByRuleSetId(ruleSetId: Long)
    fun deleteByUnitTypeId(unitTypeId: Long)
}