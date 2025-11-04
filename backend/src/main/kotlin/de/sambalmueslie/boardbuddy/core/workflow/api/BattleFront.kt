package de.sambalmueslie.boardbuddy.core.workflow.api

import de.sambalmueslie.boardbuddy.core.unit.api.UnitInstance

data class BattleFront(
    val index: Int,
    val unit: UnitInstance,
    val remainingHealth: Int,
    val defeated: Boolean,
)
