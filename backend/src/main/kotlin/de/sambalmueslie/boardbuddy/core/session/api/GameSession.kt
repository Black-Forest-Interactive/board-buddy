package de.sambalmueslie.boardbuddy.core.session.api

import de.sambalmueslie.boardbuddy.core.game.api.Game
import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSet
import java.time.LocalDateTime

data class GameSession(
    val id: Long,
    val key: String,
    val host: Player,
    val participants: List<Player>,
    val game: Game,
    val ruleSet: RuleSet,
    val timestamp: LocalDateTime
)
