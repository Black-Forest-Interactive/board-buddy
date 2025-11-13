package de.sambalmueslie.boardbuddy.core.unit.api

import de.sambalmueslie.boardbuddy.common.EntityChangeRequest
import de.sambalmueslie.boardbuddy.engine.api.UnitType

data class UnitDefinitionChangeRequest(
    val name: String,
    val unitType: UnitType,
    val counterClass: UnitType?,
    val damagePoints: PointsRange,
    val healthPoints: PointsRange,
    val maxLevel: Int,
) : EntityChangeRequest
