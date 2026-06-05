package de.sambalmueslie.boardbuddy.core.workflow

import de.sambalmueslie.boardbuddy.core.game.GameService
import de.sambalmueslie.boardbuddy.core.game.api.GameChangeRequest
import de.sambalmueslie.boardbuddy.core.nation.NationService
import de.sambalmueslie.boardbuddy.core.nation.api.NationChangeRequest
import de.sambalmueslie.boardbuddy.core.player.PlayerService
import de.sambalmueslie.boardbuddy.core.player.api.PlayerChangeRequest
import de.sambalmueslie.boardbuddy.core.player.api.PlayerType
import de.sambalmueslie.boardbuddy.core.ruleset.RuleSetService
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSetChangeRequest
import de.sambalmueslie.boardbuddy.core.unit.UnitDefinitionService
import de.sambalmueslie.boardbuddy.core.unit.api.PointsRange
import de.sambalmueslie.boardbuddy.core.unit.api.UnitDefinitionChangeRequest
import de.sambalmueslie.boardbuddy.engine.api.UnitType
import de.sambalmueslie.boardbuddy.workflow.WorkflowService
import de.sambalmueslie.boardbuddy.workflow.api.WorkflowAssignPlayerRequest
import de.sambalmueslie.boardbuddy.workflow.api.WorkflowCreateRequest
import de.sambalmueslie.boardbuddy.workflow.api.WorkflowCreateUnitRequest
import de.sambalmueslie.boardbuddy.workflow.battle.api.*
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Reproduces the stuck-battle bug:
 *
 * When player A (defender, 2 units) and player B (attacker, 3 units) play the sequence
 *   1. A opens a front (1 unit deployed)
 *   2. B opens a front (1 unit deployed)
 *   3. A attacks B's front (last unit deployed — A reserve = 0)
 *   4. B opens another front (1 unit deployed)
 *
 * `switchActivePlayer` hands the turn back to A, but A has no reserve units left.
 * Neither `createFront` nor `attackFront` is possible for A → battle deadlocks.
 *
 * After the fix, the active player must always be able to act (have reserve units).
 */
@MicronautTest
class WorkflowBattleStuckTest {

    @Inject lateinit var service: WorkflowService
    @Inject lateinit var gameService: GameService
    @Inject lateinit var playerService: PlayerService
    @Inject lateinit var ruleSetService: RuleSetService
    @Inject lateinit var nationService: NationService
    @Inject lateinit var unitDefinitionService: UnitDefinitionService

    @Test
    fun `battle must not hand turn to a player with no reserve units`() {
        // ── setup ────────────────────────────────────────────────────────────
        var ruleSet = ruleSetService.create(RuleSetChangeRequest("stuck-test-ruleset"))
        val inf = unitDefinitionService.create(UnitDefinitionChangeRequest("inf-stuck", UnitType.INFANTRY,  UnitType.MOUNTED,   PointsRange(1, 3), PointsRange(1, 3), 4))
        val cav = unitDefinitionService.create(UnitDefinitionChangeRequest("cav-stuck", UnitType.MOUNTED,   UnitType.ARTILLERY, PointsRange(1, 3), PointsRange(1, 3), 4))
        val art = unitDefinitionService.create(UnitDefinitionChangeRequest("art-stuck", UnitType.ARTILLERY, UnitType.INFANTRY,  PointsRange(1, 3), PointsRange(1, 3), 4))
        ruleSet = ruleSetService.assignUnitDefinition(ruleSet, inf)!!
        ruleSet = ruleSetService.assignUnitDefinition(ruleSet, cav)!!
        ruleSet = ruleSetService.assignUnitDefinition(ruleSet, art)!!

        val nationA = nationService.create(NationChangeRequest("nation-A-stuck", "desc", ""))
        val nationB = nationService.create(NationChangeRequest("nation-B-stuck", "desc", ""))
        ruleSet = ruleSetService.assignNation(ruleSet, nationA)!!
        ruleSet = ruleSetService.assignNation(ruleSet, nationB)!!

        var game = gameService.create(GameChangeRequest("game-stuck", "default"))
        game = gameService.assignRuleSet(game, ruleSet)!!

        val pA = playerService.create(PlayerChangeRequest(PlayerType.HUMAN, "playerA-stuck"))
        val pB = playerService.create(PlayerChangeRequest(PlayerType.HUMAN, "playerB-stuck"))

        // pA = defender → goes first in ARMY_VS_ARMY
        var workflow = service.create(WorkflowCreateRequest("workflow-stuck", pA.id, game.id, ruleSet.id, nationA.id))
        workflow = service.assign(workflow.id, WorkflowAssignPlayerRequest(pB.id, nationB.id))

        // A gets 2 units, B gets 3 units
        workflow = service.createUnit(workflow.id, WorkflowCreateUnitRequest(pA.id, inf.id))
        workflow = service.createUnit(workflow.id, WorkflowCreateUnitRequest(pA.id, cav.id))

        workflow = service.createUnit(workflow.id, WorkflowCreateUnitRequest(pB.id, inf.id))
        workflow = service.createUnit(workflow.id, WorkflowCreateUnitRequest(pB.id, cav.id))
        workflow = service.createUnit(workflow.id, WorkflowCreateUnitRequest(pB.id, art.id))

        // ── start battle: pB = attacker, pA = defender ───────────────────────
        workflow = service.battleStart(
            workflow.id,
            WorkflowBattleStartRequest(
                BattleParticipantRequest(pB.id, 1),  // attacker
                BattleParticipantRequest(pA.id, 1),  // defender → goes first
                BattleType.ARMY_VS_ARMY,
                false
            )
        )

        val initialBattle = workflow.activeBattle!!
        assertEquals(pA.id, initialBattle.activePlayer.player.id, "Defender (A) should be active first")

        val bpA = initialBattle.participant.first { it.player.player.id == pA.id }
        val bpB = initialBattle.participant.first { it.player.player.id == pB.id }
        assertEquals(2, bpA.units.size, "A should have exactly 2 units")
        assertEquals(3, bpB.units.size, "B should have exactly 3 units")

        val aUnits = service.getUnits(bpA)
        val bUnits = service.getUnits(bpB)
        val aUnit1 = aUnits[0]  // A's first unit  → deployed to own front
        val aUnit2 = aUnits[1]  // A's second unit → used to attack B's front (A's last unit)
        val bUnit1 = bUnits[0]  // B's first unit  → deployed to own front
        val bUnit2 = bUnits[1]  // B's second unit → deployed to a second front

        // ── step 1: A opens front (front index 1) ────────────────────────────
        workflow = service.battleCreateFront(
            workflow.id,
            WorkflowBattleCreateFrontRequest(pA.id, aUnit1.entity)
        )
        assertEquals(pB.id, workflow.activeBattle!!.activePlayer.player.id, "After A opens front, B should be active")

        // ── step 2: B opens front (front index 2) ────────────────────────────
        workflow = service.battleCreateFront(
            workflow.id,
            WorkflowBattleCreateFrontRequest(pB.id, bUnit1.entity)
        )
        assertEquals(pA.id, workflow.activeBattle!!.activePlayer.player.id, "After B opens front, A should be active")

        // ── step 3: A attacks B's front (index 2) with aUnit2 ────────────────
        // aUnit2 is A's last reserve unit — after this A.units = []
        val battleAfterAttack = service.battleAttackFront(
            workflow.id,
            WorkflowBattleAttackFrontRequest(pA.id, pB.id, aUnit2.entity, 2)
        )
        assertEquals(pB.id, battleAfterAttack.activePlayer.player.id, "After A attacks, B should be active")

        // ── step 4: B opens another front (index 3) with bUnit2 ──────────────
        // After this action switchActivePlayer gives the turn to A,
        // but A has 0 reserve units → battle is stuck (bug).
        workflow = service.battleCreateFront(
            workflow.id,
            WorkflowBattleCreateFrontRequest(pB.id, bUnit2.entity)
        )

        val finalBattle = workflow.activeBattle!!

        // ── assertion: active player must be able to act ──────────────────────
        // The fix must ensure that when a player has no reserve units the turn
        // is NOT handed to them.  Either the turn skips to the player who can
        // still act, or the battle is resolved immediately.
        val activeParticipant = finalBattle.participant.first { it.player.player.id == finalBattle.activePlayer.player.id }
        val activeUnits = service.getUnits(activeParticipant)
        assertTrue(
            activeUnits.isNotEmpty(),
            "Active player '${finalBattle.activePlayer.player.name}' has no reserve units — " +
            "battle is stuck. switchActivePlayer must skip players with an empty reserve."
        )
    }
}
