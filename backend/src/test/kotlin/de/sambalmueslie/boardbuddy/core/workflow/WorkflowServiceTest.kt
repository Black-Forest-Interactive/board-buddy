package de.sambalmueslie.boardbuddy.core.workflow

import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.game.GameService
import de.sambalmueslie.boardbuddy.core.game.api.GameChangeRequest
import de.sambalmueslie.boardbuddy.core.player.PlayerService
import de.sambalmueslie.boardbuddy.core.player.api.PlayerChangeRequest
import de.sambalmueslie.boardbuddy.core.ruleset.RuleSetService
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSetChangeRequest
import de.sambalmueslie.boardbuddy.core.session.GameSessionService
import de.sambalmueslie.boardbuddy.core.unit.UnitTypeService
import de.sambalmueslie.boardbuddy.core.unit.api.PointsRange
import de.sambalmueslie.boardbuddy.core.unit.api.UnitDefinitionChangeRequest
import de.sambalmueslie.boardbuddy.engine.api.UnitType
import de.sambalmueslie.boardbuddy.workflow.WorkflowService
import de.sambalmueslie.boardbuddy.workflow.api.*
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Testcontainers

@MicronautTest()
@Testcontainers
class WorkflowServiceTest {
    @Inject
    lateinit var service: WorkflowService

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
    lateinit var eventService: EventService


    @Test
    fun testSimpleGame() {
        var ruleSet = ruleSetService.create(RuleSetChangeRequest("default"))

        val inf = unitTypeService.create(UnitDefinitionChangeRequest("infantery", UnitType.INFANTRY, UnitType.CAVALRY, PointsRange(1, 3), PointsRange(1, 3), 4))
        val cav = unitTypeService.create(UnitDefinitionChangeRequest("cavalery", UnitType.CAVALRY, UnitType.ARTILLERY, PointsRange(1, 3), PointsRange(1, 3), 4))
        val art = unitTypeService.create(UnitDefinitionChangeRequest("artillery", UnitType.ARTILLERY, UnitType.INFANTRY, PointsRange(1, 3), PointsRange(1, 3), 4))
        ruleSet = ruleSetService.assignUnitType(ruleSet, inf)!!
        ruleSet = ruleSetService.assignUnitType(ruleSet, cav)!!
        ruleSet = ruleSetService.assignUnitType(ruleSet, art)!!

        var game = gameService.create(GameChangeRequest("default", "default"))
        game = gameService.assignRuleSet(game, ruleSet)!!


        val p1 = playerService.create(PlayerChangeRequest("p1"))
        val p2 = playerService.create(PlayerChangeRequest("p2"))
        val p3 = playerService.create(PlayerChangeRequest("p3"))

        var workflow = service.create(WorkflowCreateRequest("workflow", p1.id, game.id, ruleSet.id))

        workflow = service.join(workflow.id, p2)
        workflow = service.join(workflow.id, p3)

        // create units
        workflow = service.createUnit(workflow.id, WorkflowCreateUnitRequest(p1.id, inf.id))
        workflow = service.createUnit(workflow.id, WorkflowCreateUnitRequest(p1.id, cav.id))
        workflow = service.createUnit(workflow.id, WorkflowCreateUnitRequest(p1.id, art.id))

        workflow = service.createUnit(workflow.id, WorkflowCreateUnitRequest(p2.id, inf.id))
        workflow = service.createUnit(workflow.id, WorkflowCreateUnitRequest(p2.id, cav.id))
        workflow = service.createUnit(workflow.id, WorkflowCreateUnitRequest(p2.id, art.id))

        workflow = service.createUnit(workflow.id, WorkflowCreateUnitRequest(p3.id, inf.id))
        workflow = service.createUnit(workflow.id, WorkflowCreateUnitRequest(p3.id, cav.id))
        workflow = service.createUnit(workflow.id, WorkflowCreateUnitRequest(p3.id, art.id))

        // run battle
        workflow = service.battleStart(workflow.id, WorkflowBattleStartRequest(p1.id, p2.id))
        val battle = workflow.activeBattle
        assertNotNull(battle)

        assertEquals(p1, battle!!.activePlayer)
        val bp1 = battle.participant.find { it.player.id == p1.id }
        assertNotNull(bp1)

        assertEquals(3, bp1!!.units.size)
        val bp1u1 = bp1.units.find { it.type == inf }!!
        assertEquals(UnitInstance(bp1u1.id, inf, bp1u1.damage, bp1u1.health, 1), bp1u1)

        val bp1u2 = bp1.units.find { it.type == cav }!!
        assertEquals(UnitInstance(bp1u2.id, cav, bp1u2.damage, bp1u2.health, 1), bp1u2)

        val bp1u3 = bp1.units.find { it.type == art }!!
        assertEquals(UnitInstance(bp1u3.id, art, bp1u3.damage, bp1u3.health, 1), bp1u3)

        val bp2 = battle.participant.find { it.player.id == p2.id }
        assertNotNull(bp2)

        assertEquals(emptyList<BattleFront>(), bp1.fronts)


        assertEquals(3, bp2!!.units.size)
        val bp2u1 = bp2.units.find { it.type == inf }!!
        assertEquals(UnitInstance(bp2u1.id, inf, bp2u1.damage, bp2u1.health, 1), bp2u1)

        val bp2u2 = bp2.units.find { it.type == cav }!!
        assertEquals(UnitInstance(bp2u2.id, cav, bp2u2.damage, bp2u2.health, 1), bp2u2)

        val bp2u3 = bp2.units.find { it.type == art }!!
        assertEquals(UnitInstance(bp2u3.id, art, bp2u3.damage, bp2u3.health, 1), bp2u3)

        assertEquals(emptyList<BattleFront>(), bp2.fronts)

        workflow = service.battleCreateFront(workflow.id, WorkflowBattleCreateFrontRequest(p1.id, bp1u1.id))
        assertEquals(listOf(BattleFront(1, bp1u1, bp1u1.health, false)), workflow.activeBattle!!.participant.find { it.player.id == p1.id }!!.fronts)
        assertEquals(p2.id, workflow.activeBattle.activePlayer.id)

        workflow = service.battleAttackFront(workflow.id, WorkflowBattleAttackFrontRequest(p2.id, p1.id, bp2u2.id, 1))
        assertEquals(p1.id, workflow.activeBattle!!.activePlayer.id)

    }
}