package de.sambalmueslie.boardbuddy.core.session.api

import de.sambalmueslie.boardbuddy.core.common.RequestValidationException
import de.sambalmueslie.boardbuddy.core.game.api.Game
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSet


sealed class GameSessionRequestValidationException(code: Int, msg: String) : RequestValidationException(GameSession::class, code, msg)

class GameSessionNameValidationFailed(value: String) : GameSessionRequestValidationException(1, "Validation failed due to invalid name '$value'")
class GameSessionRuleSetValidationFailed(game: Game, ruleSet: RuleSet) :
    GameSessionRequestValidationException(2, "Validation failed due to invalid rule set '${ruleSet.name}' which is not part of ${game.name}")