package de.sambalmueslie.boardbuddy.engine

import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.core.player.api.PlayerType
import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.engine.system.BattleHandSystem
import de.sambalmueslie.boardbuddy.workflow.battle.api.BattleType
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@MicronautTest
class BattleHandSystemTest {
    @Inject
    lateinit var system: BattleHandSystem

    @Inject
    lateinit var engine: GameEngine

    private val participant = GameSessionPlayer(Player(1, PlayerType.HUMAN, "p", LocalDateTime.now()), 1000L)
    private val units: List<Long> = (1L..30L).toList()

    @Test
    fun `base hand grows by two per additional army`() {
        assertEquals(3, system.determine(participant, 1, BattleType.ARMY_VS_ARMY, units, true).size)
        assertEquals(7, system.determine(participant, 3, BattleType.ARMY_VS_ARMY, units, true).size)
    }

    @Test
    fun `bonus points increase the hand`() {
        assertEquals(5, system.determine(participant, 1, BattleType.ARMY_VS_ARMY, units, true, 2).size)
        assertEquals(10, system.determine(participant, 3, BattleType.ARMY_VS_ARMY, units, false, 3).size)
    }

    @Test
    fun `hand never drops below one and is capped by available units`() {
        assertEquals(1, system.determine(participant, 1, BattleType.ARMY_VS_ARMY, units, true, -10).size)
        assertEquals(30, system.determine(participant, 1, BattleType.ARMY_VS_ARMY, units, true, 100).size)
    }

    @Test
    fun `city defender gets three additional cards, attacker does not`() {
        assertEquals(6, engine.determineDefenderUnits(participant, 1, BattleType.ARMY_VS_CITY, units).size)
        assertEquals(3, engine.determineAttackerUnits(participant, 1, BattleType.ARMY_VS_CITY, units).size)
        assertEquals(8, engine.determineDefenderUnits(participant, 1, BattleType.ARMY_VS_CITY, units, 2).size)
    }
}
