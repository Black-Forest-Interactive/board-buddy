package de.sambalmueslie.boardbuddy.core.session.db

import jakarta.persistence.Entity
import jakarta.persistence.Table

@Suppress("JpaMissingIdInspection")
@Entity(name = "GameSessionUnit")
@Table(name = "game_session_unit")
data class GameSessionUnitRelation(
    val gameSessionId: Long,
    val playerId: Long,
    val unitInstanceId: Long
)
