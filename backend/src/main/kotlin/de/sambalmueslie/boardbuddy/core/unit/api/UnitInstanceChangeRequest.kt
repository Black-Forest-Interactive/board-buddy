package de.sambalmueslie.boardbuddy.core.unit.api

import de.sambalmueslie.boardbuddy.common.EntityChangeRequest

data class UnitInstanceChangeRequest(
    val type: UnitDefinition,
    val damage: Int,
    val health: Int,
    val level: Int
) : EntityChangeRequest
