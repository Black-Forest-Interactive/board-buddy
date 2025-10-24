package de.sambalmueslie.boardbuddy.core.game.db

import de.sambalmueslie.boardbuddy.core.game.api.Game
import de.sambalmueslie.boardbuddy.core.game.api.GameChangeRequest
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import java.time.LocalDateTime

data class GameData(
    @Id @GeneratedValue var id: Long,
    var name: String,
    var description: String,
    var created: LocalDateTime,
    var updated: LocalDateTime? = null
) {
    fun convert() = Game(id, name, description)
    fun update(request: GameChangeRequest, currentTime: LocalDateTime): GameData {
        name = request.name
        description = request.description
        updated = currentTime
        return this
    }
}
