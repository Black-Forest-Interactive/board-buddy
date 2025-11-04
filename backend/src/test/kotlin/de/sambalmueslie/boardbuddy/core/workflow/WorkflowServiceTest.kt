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
import de.sambalmueslie.boardbuddy.core.unit.api.UnitClass
import de.sambalmueslie.boardbuddy.core.unit.api.UnitTypeChangeRequest
import de.sambalmueslie.boardbuddy.core.workflow.api.WorkflowBattleCreateFrontRequest
import de.sambalmueslie.boardbuddy.core.workflow.api.WorkflowBattleStartRequest
import de.sambalmueslie.boardbuddy.core.workflow.api.WorkflowCreateRequest
import de.sambalmueslie.boardbuddy.core.workflow.api.WorkflowCreateUnitRequest
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
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

        var participant = battle!!.participant.find { it.player == battle.activePlayer }
        assertNotNull(participant)
        workflow = service.battleCreateFront(workflow.id, WorkflowBattleCreateFrontRequest(battle.activePlayer.id, participant!!.units.first().id))

    }
}