package de.sambalmueslie.boardbuddy.core.session.api

import de.sambalmueslie.boardbuddy.common.EntityChangeRequest
import de.sambalmueslie.boardbuddy.core.game.api.Game
import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSet
import de.sambalmueslie.boardbuddy.engine.api.GameEntity

data class GameSessionChangeRequest(
    val name: String,
    val host: Player,
    val hostEntity: GameEntity,
    val game: Game,
    val ruleSet: RuleSet
) : EntityChangeRequest
