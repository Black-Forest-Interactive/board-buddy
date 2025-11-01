package de.sambalmueslie.boardbuddy.core.engine

import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.core.unit.api.UnitType
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class GameEngine(

) {


    companion object {
        private val logger = LoggerFactory.getLogger(GameEngine::class.java)
    }

    fun createUnit(player: Player, session: GameSession, unitType: UnitType) {

        TODO("Not yet implemented")
    }
}