package de.sambalmueslie.boardbuddy.core.ruleset.api

import de.sambalmueslie.boardbuddy.core.common.RequestValidationException


sealed class RuleSetRequestValidationException(code: Int, msg: String) : RequestValidationException(RuleSet::class, code, msg)

class RuleSetNameValidationFailed(value: String) : RuleSetRequestValidationException(1, "Validation failed due to invalid name '$value'")