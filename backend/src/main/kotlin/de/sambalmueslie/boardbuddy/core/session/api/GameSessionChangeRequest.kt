package de.sambalmueslie.boardbuddy.core.session.api

import de.sambalmueslie.boardbuddy.core.common.EntityChangeRequest
import de.sambalmueslie.boardbuddy.core.game.api.Game
import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSet

data class GameSessionChangeRequest(
    val name: String,
    val host: Player,
    val game: Game,
    val ruleSet: RuleSet
) : EntityChangeRequest
