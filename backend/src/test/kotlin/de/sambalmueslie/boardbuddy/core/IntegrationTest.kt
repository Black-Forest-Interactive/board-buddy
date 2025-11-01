package de.sambalmueslie.boardbuddy.core

import de.sambalmueslie.boardbuddy.core.engine.GameEngine
import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.game.GameService
import de.sambalmueslie.boardbuddy.core.game.api.GameChangeRequest
import de.sambalmueslie.boardbuddy.core.player.PlayerService
import de.sambalmueslie.boardbuddy.core.player.api.PlayerChangeRequest
import de.sambalmueslie.boardbuddy.core.ruleset.RuleSetService
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSetChangeRequest
import de.sambalmueslie.boardbuddy.core.session.GameSessionService
import de.sambalmueslie.boardbuddy.core.session.api.GameSessionChangeRequest
import de.sambalmueslie.boardbuddy.core.unit.UnitTypeService
import de.sambalmueslie.boardbuddy.core.unit.api.PointsRange
import de.sambalmueslie.boardbuddy.core.unit.api.UnitClass
import de.sambalmueslie.boardbuddy.core.unit.api.UnitTypeChangeRequest
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Testcontainers

@MicronautTest()
@Testcontainers
class IntegrationTest {
    @Inject
    lateinit var gameService: GameService

    @Inject
    lateinit var playerService: PlayerService

    @Inject
    lateinit var ruleSetService: RuleSetService

    @Inject
    lateinit var sessionService: GameSessionService

    @Inject
    lateinit var unitTypeService: UnitTypeService

    @Inject
    lateinit var engine: GameEngine

    @Inject
    lateinit var eventService: EventService

    @Test
    fun testSimpleGame() {
        var ruleSet = ruleSetService.create(RuleSetChangeRequest("default"))

        val inf = unitTypeService.create(UnitTypeChangeRequest("infantery", UnitClass.INFANTRY, UnitClass.CAVALRY, PointsRange(1, 3), PointsRange(1, 3), 4))
        val cav = unitTypeService.create(UnitTypeChangeRequest("cavalery", UnitClass.CAVALRY, UnitClass.ARTILLERY, PointsRange(1, 3), PointsRange(1, 3), 4))
        val art = unitTypeService.create(UnitTypeChangeRequest("artillery", UnitClass.ARTILLERY, UnitClass.INFANTRY, PointsRange(1, 3), PointsRange(1, 3), 4))
        ruleSet = ruleSetService.assignUnitType(ruleSet, inf)!!
        ruleSet = ruleSetService.assignUnitType(ruleSet, cav)!!
        ruleSet = ruleSetService.assignUnitType(ruleSet, art)!!

        var game = gameService.create(GameChangeRequest("default", "default"))
        game = gameService.assignRuleSet(game, ruleSet)!!

        val p1 = playerService.create(PlayerChangeRequest("p1"))
        val p2 = playerService.create(PlayerChangeRequest("p2"))
        val p3 = playerService.create(PlayerChangeRequest("p3"))

        val session = sessionService.create(GameSessionChangeRequest("default", p1, game, ruleSet))
        sessionService.assignPlayer(session, p2)
        sessionService.assignPlayer(session, p3)

        engine.createUnit(p1, session, inf)
        engine.createUnit(p1, session, cav)
        engine.createUnit(p1, session, art)

        engine.createUnit(p2, session, inf)
        engine.createUnit(p2, session, cav)
        engine.createUnit(p2, session, art)

        engine.createUnit(p3, session, inf)
        engine.createUnit(p3, session, cav)
        engine.createUnit(p3, session, art)
    }

}