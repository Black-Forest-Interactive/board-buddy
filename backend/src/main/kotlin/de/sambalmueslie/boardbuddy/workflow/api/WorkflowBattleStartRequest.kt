package de.sambalmueslie.boardbuddy.workflow.api

data class WorkflowBattleStartRequest(
    val attacker: BattleParticipantRequest,
    val defender: BattleParticipantRequest,
    val type: BattleType,
    val isWalled: Boolean
)


