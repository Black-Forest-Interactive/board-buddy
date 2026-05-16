package de.sambalmueslie.boardbuddy.workflow.battle

internal data class BattleFrontData(
    val index: Int,
    val units: MutableList<BattleFrontUnitData> = mutableListOf()
)