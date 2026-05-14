package de.sambalmueslie.boardbuddy.workflow.api

import de.sambalmueslie.boardbuddy.engine.api.GameUnit

data class BattleFrontInfo(
    val index: Int,
    val unit: GameUnit
)
