package de.sambalmueslie.boardbuddy.core.unit.api

import de.sambalmueslie.boardbuddy.core.common.EntityChangeRequest

data class UnitTypeChangeRequest(
    val name: String,
    val unitClass: UnitClass,
    val counterClass: UnitClass?,
    val damagePoints: PointsRange,
    val healthPoints: PointsRange,
    val maxLevel: Int,
) : EntityChangeRequest
