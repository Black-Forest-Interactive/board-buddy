package de.sambalmueslie.boardbuddy.core.unit.api

import de.sambalmueslie.boardbuddy.core.common.EntityChangeRequest

data class UnitInstanceChangeRequest(
    val type: UnitType,
    val damage: Int,
    val health: Int,
    val level: Int
) : EntityChangeRequest
