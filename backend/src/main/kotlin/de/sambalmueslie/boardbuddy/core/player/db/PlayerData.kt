package de.sambalmueslie.boardbuddy.core.player.db

import de.sambalmueslie.boardbuddy.common.EntityData
import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.core.player.api.PlayerChangeRequest
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity(name = "Player")
@Table(name = "player")
data class PlayerData(
    @Id @GeneratedValue var id: Long,
    var name: String,
    var created: LocalDateTime,
    var updated: LocalDateTime? = null
) : EntityData {
    fun convert() = Player(id, name)
    fun update(request: PlayerChangeRequest, currentTime: LocalDateTime): PlayerData {
        name = request.name
        updated = currentTime
        return this
    }
}

