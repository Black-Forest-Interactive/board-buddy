package de.sambalmueslie.boardbuddy.core.ruleset.db

import jakarta.persistence.Entity
import jakarta.persistence.Table

@Suppress("JpaMissingIdInspection")
@Entity(name = "RuleSetUnitType")
@Table(name = "rule_set_unit_type")
data class RuleSetUnitTypeRelation(
    val ruleSetId: Long,
    val unitTypeId: Long
)
