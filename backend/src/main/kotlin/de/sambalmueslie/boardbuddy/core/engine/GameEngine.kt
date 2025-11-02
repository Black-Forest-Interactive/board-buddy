package de.sambalmueslie.boardbuddy.core.engine

import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.core.unit.UnitInstanceService
import de.sambalmueslie.boardbuddy.core.unit.api.UnitInstance
import de.sambalmueslie.boardbuddy.core.unit.api.UnitInstanceChangeRequest
import de.sambalmueslie.boardbuddy.core.unit.api.UnitType
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import kotlin.random.Random

@Singleton
class GameEngine(
    private val unitInstanceService: UnitInstanceService
) {


    companion object {
        private val logger = LoggerFactory.getLogger(GameEngine::class.java)
    }

    fun createUnit(player: Player, session: GameSession, unitType: UnitType): UnitInstance {
        // TODO calculate damage and health more smart
        val damage = Random.nextInt(unitType.damagePoints.min, unitType.damagePoints.max + 1)
        val health = Random.nextInt(unitType.healthPoints.min, unitType.healthPoints.max + 1)
        // TODO consider level
        val request = UnitInstanceChangeRequest(unitType, damage, health, 1)
        return unitInstanceService.create(request)
    }
}