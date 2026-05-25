package de.sambalmueslie.boardbuddy.core.ruleset.db

import jakarta.persistence.Entity
import jakarta.persistence.Table

@Suppress("JpaMissingIdInspection")
@Entity(name = "RuleSetTechnology")
@Table(name = "rule_set_technology")
data class RuleSetTechnologyRelation(
    var ruleSetId: Long,
    var technologyId: Long
)
