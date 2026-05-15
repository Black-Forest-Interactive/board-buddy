package de.sambalmueslie.boardbuddy.workflow.api

import de.sambalmueslie.boardbuddy.engine.api.CombatAction

data class BattleAttackResult(
    val workflow: Workflow,
    val actions: List<CombatAction>
)
