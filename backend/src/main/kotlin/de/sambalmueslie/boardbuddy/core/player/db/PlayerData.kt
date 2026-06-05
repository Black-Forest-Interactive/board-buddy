package de.sambalmueslie.boardbuddy.core.player.db

import de.sambalmueslie.boardbuddy.common.EntityData
import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.core.player.api.PlayerChangeRequest
import de.sambalmueslie.boardbuddy.core.player.api.PlayerType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity(name = "Player")
@Table(name = "player")
data class PlayerData(
    @Id @GeneratedValue var id: Long,
    var name: String,
    @Enumerated(EnumType.STRING) var type: PlayerType,
    var created: LocalDateTime,
    var updated: LocalDateTime? = null
) : EntityData {
    fun convert() = Player(id, type, name, updated ?: created)
    fun update(request: PlayerChangeRequest, currentTime: LocalDateTime): PlayerData {
        name = request.name
        type = request.type
        updated = currentTime
        return this
    }
}

