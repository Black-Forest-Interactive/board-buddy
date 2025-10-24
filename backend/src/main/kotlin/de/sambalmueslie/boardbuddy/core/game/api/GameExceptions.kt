package de.sambalmueslie.boardbuddy.core.game.api

import de.sambalmueslie.boardbuddy.core.common.RequestValidationException


sealed class GameRequestValidationException(code: Int, msg: String) : RequestValidationException(Game::class, code, msg)

class NameValidationFailed(value: String) : GameRequestValidationException(1, "Validation failed due to invalid name '$value'")

class DescriptionValidationFailed(value: String) : GameRequestValidationException(2, "Validation failed due to invalid description '$value'")