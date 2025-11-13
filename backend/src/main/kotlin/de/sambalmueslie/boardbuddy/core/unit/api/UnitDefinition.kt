package de.sambalmueslie.boardbuddy.core.unit.api

import de.sambalmueslie.boardbuddy.common.Entity
import de.sambalmueslie.boardbuddy.engine.api.UnitType

data class UnitDefinition(
    override val id: Long,
    val name: String,
    val unitType: UnitType,
    val counterType: UnitType?,
    val damagePoints: PointsRange,
    val healthPoints: PointsRange,
    val maxLevel: Int,
) : Entity
