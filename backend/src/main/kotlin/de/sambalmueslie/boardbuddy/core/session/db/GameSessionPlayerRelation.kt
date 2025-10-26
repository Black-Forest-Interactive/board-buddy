package de.sambalmueslie.boardbuddy.core.session.db

import jakarta.persistence.Entity
import jakarta.persistence.Table

@Suppress("JpaMissingIdInspection")
@Entity(name = "GameSessionPlayer")
@Table(name = "game_session_player")
data class GameSessionPlayerRelation(
    val gameSessionId: Long,
    val playerId: Long,
)
