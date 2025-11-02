package de.sambalmueslie.boardbuddy.core.unit.api

import de.sambalmueslie.boardbuddy.core.common.EntityException


sealed class UnitTypeRequestValidationException(code: Int, msg: String) : EntityException(UnitType::class, code, msg)

class UnitTypeNameValidationFailed(value: String) : UnitTypeRequestValidationException(1, "Validation failed due to invalid name '$value'")
class UnitTypeCounterClassValidationFailed(value: UnitClass?) : UnitTypeRequestValidationException(2, "Validation failed due to invalid counter class '$value'")
class UnitTypeDamageValidationFailed(value: PointsRange) : UnitTypeRequestValidationException(2, "Validation failed due to invalid damage '$value'")
class UnitTypeHealthValidationFailed(value: PointsRange) : UnitTypeRequestValidationException(3, "Validation failed due to invalid health '$value'")
class UnitTypeLevelValidationFailed(value: Int) : UnitTypeRequestValidationException(4, "Validation failed due to invalid level '$value'")