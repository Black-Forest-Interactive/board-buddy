package de.sambalmueslie.boardbuddy.engine.api

interface CombatParticipant {
    val unit: GameEntity
    var currentHealth: Int
}