package de.sambalmueslie.boardbuddy.core.nation.api

import de.sambalmueslie.boardbuddy.common.EntityException


sealed class NationRequestValidationException(code: Int, msg: String) : EntityException(Nation::class, code, msg)

class NationNameValidationFailed(value: String) : NationRequestValidationException(1, "Validation failed due to invalid name '$value'")