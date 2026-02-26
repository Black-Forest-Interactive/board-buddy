package de.sambalmueslie.boardbuddy.core.unit.api

import de.sambalmueslie.boardbuddy.common.EntityException
import de.sambalmueslie.boardbuddy.engine.api.UnitType


sealed class UnitDefinitionRequestValidationException(code: Int, msg: String) : EntityException(UnitDefinition::class, code, msg)

class UnitDefinitionNameValidationFailed(value: String) : UnitDefinitionRequestValidationException(1, "Validation failed due to invalid name '$value'")
class UnitDefinitionCounterClassValidationFailed(value: UnitType?) : UnitDefinitionRequestValidationException(2, "Validation failed due to invalid counter class '$value'")
class UnitDefinitionDamageValidationFailed(value: PointsRange) : UnitDefinitionRequestValidationException(2, "Validation failed due to invalid damage '$value'")
class UnitDefinitionHealthValidationFailed(value: PointsRange) : UnitDefinitionRequestValidationException(3, "Validation failed due to invalid health '$value'")
class UnitDefinitionLevelValidationFailed(value: Int) : UnitDefinitionRequestValidationException(4, "Validation failed due to invalid level '$value'")