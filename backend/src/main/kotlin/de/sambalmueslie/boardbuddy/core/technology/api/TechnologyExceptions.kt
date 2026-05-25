package de.sambalmueslie.boardbuddy.core.technology.api

import de.sambalmueslie.boardbuddy.common.EntityException


sealed class TechnologyRequestValidationException(code: Int, msg: String) : EntityException(Technology::class, code, msg)

class TechnologyNameValidationFailed(value: String) : TechnologyRequestValidationException(1, "Validation failed due to invalid name '$value'")
class TechnologyTierValidationFailed(value: Int) : TechnologyRequestValidationException(1, "Validation failed due to invalid tier '$value'")