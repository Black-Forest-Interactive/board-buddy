package de.sambalmueslie.boardbuddy.core.game.api

import de.sambalmueslie.boardbuddy.common.EntityException
import de.sambalmueslie.boardbuddy.common.forMessage


sealed class GameRequestValidationException(code: Int, msg: String) : EntityException(Game::class, code, msg)

class GameNameValidationFailed(value: String) : GameRequestValidationException(1, "Validation failed due to invalid name '${value.forMessage()}'")

class GameDescriptionValidationFailed(value: String) : GameRequestValidationException(2, "Validation failed due to invalid description '${value.forMessage()}'")