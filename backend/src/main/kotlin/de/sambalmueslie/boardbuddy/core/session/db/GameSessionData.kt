package de.sambalmueslie.boardbuddy.core.session.db

import de.sambalmueslie.boardbuddy.core.common.EntityData
import de.sambalmueslie.boardbuddy.core.game.api.Game
import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSet
import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.core.session.api.GameSessionChangeRequest
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity(name = "GameSession")
@Table(name = "game_session")
data class GameSessionData(
    @Id @GeneratedValue var id: Long,
    @GeneratedValue var key: String,
    var name: String,
    var hostId: Long,
    var gameId: Long,
    var ruleSetId: Long,
    var created: LocalDateTime,
    var updated: LocalDateTime? = null
) : EntityData {
    fun convert(host: Player, participants: List<Player>, game: Game, ruleSet: RuleSet) = GameSession(id, key, name, host, participants, game, ruleSet, updated ?: created)
    fun update(request: GameSessionChangeRequest, currentTime: LocalDateTime): GameSessionData {
        name = request.name
        hostId = request.host.id
        gameId = request.game.id
        ruleSetId = request.ruleSet.id
        updated = currentTime
        return this
    }
}

