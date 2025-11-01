package de.sambalmueslie.boardbuddy.core.unit.api

import de.sambalmueslie.boardbuddy.core.common.Entity

data class UnitInstance(
    override val id: Long,
    val type: UnitType,
    val damage: Int,
    val health: Int,
    val level: Int
) : Entity