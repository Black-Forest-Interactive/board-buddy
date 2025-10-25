package de.sambalmueslie.boardbuddy.core.player.api

import de.sambalmueslie.boardbuddy.core.common.RequestValidationException


sealed class PlayerRequestValidationException(code: Int, msg: String) : RequestValidationException(Player::class, code, msg)

class PlayerNameValidationFailed(value: String) : PlayerRequestValidationException(1, "Validation failed due to invalid name '$value'")