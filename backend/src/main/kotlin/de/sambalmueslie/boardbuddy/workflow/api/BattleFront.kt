package de.sambalmueslie.boardbuddy.workflow.api

data class BattleFront(
    val index: Int,
    val unit: UnitInstance,
    val remainingHealth: Int,
    val defeated: Boolean,
)
