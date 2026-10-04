package de.sambalmueslie.boardbuddy.workflow.battle.api

data class BattleParticipantRequest(
    val id: Long,
    val armyCount: Int,
    // extra battle points (e.g. from cards not modelled yet), added to the battle hand size
    val bonusPoints: Int = 0,
)