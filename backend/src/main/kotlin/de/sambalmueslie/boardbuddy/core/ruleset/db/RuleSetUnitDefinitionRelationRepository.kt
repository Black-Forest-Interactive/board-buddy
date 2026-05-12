package de.sambalmueslie.boardbuddy.core.ruleset.db

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.GenericRepository

@JdbcRepository(dialect = Dialect.POSTGRES)
interface RuleSetUnitDefinitionRelationRepository : GenericRepository<RuleSetUnitDefinitionRelation, Long> {
    fun save(data: RuleSetUnitDefinitionRelation): RuleSetUnitDefinitionRelation
    fun findByRuleSetId(ruleSetId: Long): List<RuleSetUnitDefinitionRelation>
    fun findByRuleSetIdAndUnitDefinitionId(ruleSetId: Long, unitDefinitionId: Long): RuleSetUnitDefinitionRelation?
    fun deleteByRuleSetIdAndUnitDefinitionId(ruleSetId: Long, unitDefinitionId: Long)
    fun deleteByRuleSetId(ruleSetId: Long)
    fun deleteByUnitDefinitionId(unitDefinitionId: Long)
}