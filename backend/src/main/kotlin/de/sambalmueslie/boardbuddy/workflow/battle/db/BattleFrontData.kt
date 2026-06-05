package de.sambalmueslie.boardbuddy.workflow.battle.db

data class BattleFrontData(
    val index: Int,
    val units: MutableList<BattleFrontUnitData> = mutableListOf()
)