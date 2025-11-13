package de.sambalmueslie.boardbuddy.core.unit.api

import de.sambalmueslie.boardbuddy.common.Entity

data class UnitInstance(
    override val id: Long,
    val type: UnitDefinition,
    val damage: Int,
    val health: Int,
    val level: Int
) : Entity