package de.sambalmueslie.boardbuddy.core.unit.api

import de.sambalmueslie.boardbuddy.core.common.Entity

data class UnitType(
    override val id: Long,
    val name: String,
    val unitClass: UnitClass,
    val counterClass: UnitClass?,
    val damagePoints: PointsRange,
    val healthPoints: PointsRange,
    val maxLevel: Int,
) : Entity
