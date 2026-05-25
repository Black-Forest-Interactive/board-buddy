package de.sambalmueslie.boardbuddy.core.ruleset.db

import jakarta.persistence.Entity
import jakarta.persistence.Table

@Suppress("JpaMissingIdInspection")
@Entity(name = "RuleSetNation")
@Table(name = "rule_set_nation")
data class RuleSetNationRelation(
    var ruleSetId: Long,
    var nationId: Long
)
