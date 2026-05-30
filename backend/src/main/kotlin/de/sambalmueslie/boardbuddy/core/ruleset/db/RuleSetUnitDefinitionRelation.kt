package de.sambalmueslie.boardbuddy.core.ruleset.db

import jakarta.persistence.Entity
import jakarta.persistence.Table

@Suppress("JpaMissingIdInspection")
@Entity(name = "RuleSetUnitDefinition")
@Table(name = "rule_set_unit_definition")
data class RuleSetUnitDefinitionRelation(
    var ruleSetId: Long,
    var unitDefinitionId: Long
)
