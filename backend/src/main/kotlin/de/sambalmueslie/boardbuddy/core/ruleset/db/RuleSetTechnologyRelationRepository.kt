package de.sambalmueslie.boardbuddy.core.ruleset.db

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.GenericRepository

@JdbcRepository(dialect = Dialect.POSTGRES)
interface RuleSetTechnologyRelationRepository : GenericRepository<RuleSetTechnologyRelation, Long> {
    fun save(data: RuleSetTechnologyRelation): RuleSetTechnologyRelation
    fun findByRuleSetId(ruleSetId: Long): List<RuleSetTechnologyRelation>
    fun findByRuleSetIdAndTechnologyId(ruleSetId: Long, technologyId: Long): RuleSetTechnologyRelation?
    fun deleteByRuleSetIdAndTechnologyId(ruleSetId: Long, technologyId: Long)
    fun deleteByRuleSetId(ruleSetId: Long)
    fun deleteByTechnologyId(technologyId: Long)
}