package de.sambalmueslie.boardbuddy.core.session.db

import jakarta.persistence.Entity
import jakarta.persistence.Table

@Suppress("JpaMissingIdInspection")
@Entity(name = "GameSessionEntity")
@Table(name = "game_session_entity")
data class GameSessionEntityRelation(
    val gameSessionId: Long,
    val playerId: Long,
    val entityId: Long
)
