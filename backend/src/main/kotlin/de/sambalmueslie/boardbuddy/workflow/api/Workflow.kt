package de.sambalmueslie.boardbuddy.workflow.api

import de.sambalmueslie.boardbuddy.core.game.api.Game
import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSet
import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import java.time.LocalDateTime

data class Workflow(
    val id: String,
    val name: String,
    val host: Player,
    val participants: List<Player>,
    val game: Game,
    val ruleSet: RuleSet,
    val timestamp: LocalDateTime,
    val activeBattle: Battle?
) {
    companion object {
        fun create(session: GameSession, battle: Battle?): Workflow {
            return Workflow(
                session.key,
                session.name,
                session.host,
                session.participants,
                session.game,
                session.ruleSet,
                session.timestamp,
                battle
            )
        }
    }
}