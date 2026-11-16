package de.sambalmueslie.boardbuddy.core.player.api

import de.sambalmueslie.boardbuddy.common.EntityException
import de.sambalmueslie.boardbuddy.common.forMessage


sealed class PlayerRequestValidationException(code: Int, msg: String) : EntityException(Player::class, code, msg)

class PlayerNameValidationFailed(value: String) : PlayerRequestValidationException(1, "Validation failed due to invalid name '${value.forMessage()}'")