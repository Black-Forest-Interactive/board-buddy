package de.sambalmueslie.boardbuddy.core.game.db

import jakarta.persistence.Entity
import jakarta.persistence.Table

@Suppress("JpaMissingIdInspection")
@Entity(name = "GameRuleSet")
@Table(name = "game_ruleset")
data class GameRuleSetRelation(
    val gameId: Long,
    val ruleSetId: Long,
)
