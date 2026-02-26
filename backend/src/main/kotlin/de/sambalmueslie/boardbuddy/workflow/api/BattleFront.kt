package de.sambalmueslie.boardbuddy.workflow.api

import de.sambalmueslie.boardbuddy.engine.api.GameEntity

data class BattleFront(
    val index: Int,
    val unit: GameEntity
)
