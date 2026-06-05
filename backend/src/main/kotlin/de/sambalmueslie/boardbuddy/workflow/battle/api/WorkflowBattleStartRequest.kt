package de.sambalmueslie.boardbuddy.workflow.battle.api

data class WorkflowBattleStartRequest(
    val attacker: BattleParticipantRequest,
    val defender: BattleParticipantRequest,
    val type: BattleType,
    val isWalled: Boolean
)


