package de.sambalmueslie.boardbuddy.core.player.db

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity(name = "PlayerToken")
@Table(name = "player_token")
data class PlayerTokenData(
    @Id var playerId: Long,
    var tokenHash: String,
    var created: LocalDateTime,
)
