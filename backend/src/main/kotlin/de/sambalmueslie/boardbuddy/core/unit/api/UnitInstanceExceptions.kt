package de.sambalmueslie.boardbuddy.core.unit.api

import de.sambalmueslie.boardbuddy.core.common.EntityException


sealed class UnitInstanceException(code: Int, msg: String) : EntityException(UnitInstance::class, code, msg)


class UnitInstanceLevelValidationFailed(value: Int) : UnitInstanceException(1, "Validation failed due to invalid level '$value'")
class UnitInstanceDamageValidationFailed(value: Int) : UnitInstanceException(2, "Validation failed due to invalid damage '$value'")
class UnitInstanceHealthValidationFailed(value: Int) : UnitInstanceException(3, "Validation failed due to invalid health '$value'")
class CannotFindUnitType(typeId: Long) : UnitInstanceException(4, "Cannot find unit type with id: $typeId")