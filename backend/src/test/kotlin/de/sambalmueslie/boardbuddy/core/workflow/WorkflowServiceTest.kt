package de.sambalmueslie.boardbuddy.core.workflow

import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.game.GameService
import de.sambalmueslie.boardbuddy.core.game.api.GameChangeRequest
import de.sambalmueslie.boardbuddy.core.nation.NationService
import de.sambalmueslie.boardbuddy.core.nation.api.NationChangeRequest
import de.sambalmueslie.boardbuddy.core.player.PlayerService
import de.sambalmueslie.boardbuddy.core.player.api.PlayerChangeRequest
import de.sambalmueslie.boardbuddy.core.ruleset.RuleSetService
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSetChangeRequest
import de.sambalmueslie.boardbuddy.core.session.GameSessionService
import de.sambalmueslie.boardbuddy.core.unit.UnitDefinitionService
import de.sambalmueslie.boardbuddy.core.unit.api.PointsRange
import de.sambalmueslie.boardbuddy.core.unit.api.UnitDefinitionChangeRequest
import de.sambalmueslie.boardbuddy.engine.api.*
import de.sambalmueslie.boardbuddy.workflow.WorkflowService
import de.sambalmueslie.boardbuddy.workflow.api.*
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

@MicronautTest()
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
    lateinit var nationService: NationService

    @Inject
    lateinit var unitTypeService: UnitDefinitionService

    @Inject
    lateinit var eventService: EventService


    @Test
    fun testSimpleGame() {
        var ruleSet = ruleSetService.create(RuleSetChangeRequest("default"))

        val inf = unitTypeService.create(UnitDefinitionChangeRequest("infantery", UnitType.INFANTRY, UnitType.MOUNTED, PointsRange(1, 3), PointsRange(1, 3), 4))
        val cav = unitTypeService.create(UnitDefinitionChangeRequest("cavalery", UnitType.MOUNTED, UnitType.ARTILLERY, PointsRange(1, 3), PointsRange(1, 3), 4))
        val art = unitTypeService.create(UnitDefinitionChangeRequest("artillery", UnitType.ARTILLERY, UnitType.INFANTRY, PointsRange(1, 3), PointsRange(1, 3), 4))
        ruleSet = ruleSetService.assignUnitDefinition(ruleSet, inf)!!
        ruleSet = ruleSetService.assignUnitDefinition(ruleSet, cav)!!
        ruleSet = ruleSetService.assignUnitDefinition(ruleSet, art)!!

        val nation1 = nationService.create(NationChangeRequest("nation1", "desc", ""))
        val nation2 = nationService.create(NationChangeRequest("nation2", "desc", ""))
        val nation3 = nationService.create(NationChangeRequest("nation3", "desc", ""))
        ruleSet = ruleSetService.assignNation(ruleSet, nation1)!!
        ruleSet = ruleSetService.assignNation(ruleSet, nation2)!!
        ruleSet = ruleSetService.assignNation(ruleSet, nation3)!!

        var game = gameService.create(GameChangeRequest("default", "default"))
        game = gameService.assignRuleSet(game, ruleSet)!!

        val p1 = playerService.create(PlayerChangeRequest("p1"))
        val p2 = playerService.create(PlayerChangeRequest("p2"))
        val p3 = playerService.create(PlayerChangeRequest("p3"))

        var workflow = service.create(WorkflowCreateRequest("workflow", p1.id, game.id, ruleSet.id, nation1.id))

        workflow = service.assign(workflow.id, WorkflowAssignPlayerRequest(p2.id, nation2.id))
        workflow = service.assign(workflow.id, WorkflowAssignPlayerRequest(p3.id, nation3.id))

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
        workflow = service.battleStart(workflow.id, WorkflowBattleStartRequest(BattleParticipantRequest(p1.id, 1), BattleParticipantRequest(p2.id, 1), BattleType.ARMY_VS_ARMY, false))
        val battle = workflow.activeBattle
        assertNotNull(battle)

        // defender (p2) goes first for ARMY_VS_ARMY
        assertEquals(p2.id, battle!!.activePlayer.player.id)
        assertEquals(emptyList<BattleFront>(), battle.fronts)

        val bp1 = battle.participant.find { it.player.player.id == p1.id }
        assertNotNull(bp1)
        assertEquals(3, bp1!!.units.size)
        val bp1Units = service.getUnits(bp1)

        val bp1u1 = bp1Units.find { it.type?.kind == inf.unitType }!!
        assertEquals(GameUnit(bp1u1.entity, bp1u1.damage, bp1u1.health, Level(1), Type(inf.unitType), CounterType(inf.counterType!!)), bp1u1)

        val bp1u2 = bp1Units.find { it.type?.kind == cav.unitType }!!
        assertEquals(GameUnit(bp1u2.entity, bp1u2.damage, bp1u2.health, Level(1), Type(cav.unitType), CounterType(cav.counterType!!)), bp1u2)

        val bp1u3 = bp1Units.find { it.type?.kind == art.unitType }!!
        assertEquals(GameUnit(bp1u3.entity, bp1u3.damage, bp1u3.health, Level(1), Type(art.unitType), CounterType(art.counterType!!)), bp1u3)

        val bp2 = battle.participant.find { it.player.player.id == p2.id }
        assertNotNull(bp2)
        assertEquals(3, bp2!!.units.size)
        val bp2Units = service.getUnits(bp2)

        val bp2u1 = bp2Units.find { it.type?.kind == inf.unitType }!!
        assertEquals(GameUnit(bp2u1.entity, bp2u1.damage, bp2u1.health, Level(1), Type(inf.unitType), CounterType(inf.counterType)), bp2u1)

        val bp2u2 = bp2Units.find { it.type?.kind == cav.unitType }!!
        assertEquals(GameUnit(bp2u2.entity, bp2u2.damage, bp2u2.health, Level(1), Type(cav.unitType), CounterType(cav.counterType)), bp2u2)

        val bp2u3 = bp2Units.find { it.type?.kind == art.unitType }!!
        assertEquals(GameUnit(bp2u3.entity, bp2u3.damage, bp2u3.health, Level(1), Type(art.unitType), CounterType(art.counterType)), bp2u3)

        // p2 (defender, active) creates front with infantry
        workflow = service.battleCreateFront(workflow.id, WorkflowBattleCreateFrontRequest(p2.id, bp2u1.entity))
        val battleAfterFront = workflow.activeBattle!!
        val fronts = battleAfterFront.fronts
        assertEquals(1, fronts.size)
        assertEquals(1, fronts[0].index)
        assertEquals(1, fronts[0].units.size)
        assertEquals(bp2u1.entity, fronts[0].units[0].unit.entity)
        assertEquals(p1.id, battleAfterFront.activePlayer.player.id)

        // p1 (attacker, now active) attacks front 1 with cavalry
        val battleAfterAttack = service.battleAttackFront(workflow.id, WorkflowBattleAttackFrontRequest(p1.id, p2.id, bp1u2.entity, 1))
        assertEquals(p2.id, battleAfterAttack.activePlayer.player.id)
    }
}
