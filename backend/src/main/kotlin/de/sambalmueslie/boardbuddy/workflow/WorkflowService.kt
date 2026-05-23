package de.sambalmueslie.boardbuddy.workflow

import de.sambalmueslie.boardbuddy.core.game.GameService
import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.core.ruleset.RuleSetService
import de.sambalmueslie.boardbuddy.core.session.GameSessionService
import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.core.session.api.GameSessionChangeRequest
import de.sambalmueslie.boardbuddy.engine.GameEngine
import de.sambalmueslie.boardbuddy.engine.api.GameUnit
import de.sambalmueslie.boardbuddy.engine.api.NationType
import de.sambalmueslie.boardbuddy.workflow.api.*
import de.sambalmueslie.boardbuddy.workflow.battle.WorkflowBattleService
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class WorkflowService(
    private val playerService: WorkflowPlayerService,
    private val gameService: GameService,
    private val ruleSetService: RuleSetService,
    private val unitService: WorkflowUnitService,
    private val battleService: WorkflowBattleService,
    private val researchService: WorkflowResearchService,

    private val sessionService: GameSessionService,
    private val engine: GameEngine
) {
    companion object {
        private val logger = LoggerFactory.getLogger(WorkflowService::class.java)
    }


    fun create(request: WorkflowCreateRequest): Workflow {
        val host = playerService.getHost(request.hostId)
        val game = gameService.get(request.gameId) ?: throw WorkflowInvalidGame(request.gameId)
        val ruleSet = ruleSetService.get(request.ruleSetId) ?: throw WorkflowInvalidRuleSet(request.ruleSetId)
        val hostEntity = engine.createPlayer(request.nation)
        val session = sessionService.create(GameSessionChangeRequest(request.name, host, hostEntity, game, ruleSet))

        return Workflow.create(session, null)
    }

    fun join(id: String, request: WorkflowPlayerJoinRequest): Workflow {
        val session = getSession(id)
        val playerEntity = engine.createPlayer(request.nation)
        playerService.join(session, request, playerEntity)
        return get(id)
    }

    fun join(id: String, player: Player, nation: NationType): Workflow {
        val session = getSession(id)
        val playerEntity = engine.createPlayer(nation)
        playerService.join(session, player, playerEntity)
        return get(id)
    }

    fun assignPlayer(id: String, request: WorkflowAssignPlayerRequest): Workflow {
        val player = playerService.getHost(request.playerId)
        return join(id, player, request.nation)
    }

    fun createUnit(id: String, request: WorkflowCreateUnitRequest): Workflow {
        val session = getSession(id)
        unitService.createUnit(session, request)
        return get(id)
    }

    fun battleStart(id: String, request: WorkflowBattleStartRequest): Workflow {
        val session = getSession(id)
        battleService.start(session, request)
        return get(id)
    }

    fun battleCreateFront(id: String, request: WorkflowBattleCreateFrontRequest): Workflow {
        val session = getSession(id)
        battleService.createFront(session, request)
        return get(id)
    }

    fun battleAttackFront(id: String, request: WorkflowBattleAttackFrontRequest): Battle {
        val session = getSession(id)
        return battleService.attackFront(session, request)
    }

    fun battleFinish(id: String) {
        val session = getSession(id)
        battleService.finish(session)
    }

    fun research(id: String, request: WorkflowResearchRequest): Workflow {
        val session = getSession(id)
        researchService.research(session, request)
        return get(id)
    }

    fun get(id: String): Workflow {
        val session = sessionService.findByKey(id) ?: throw WorkflowInvalidId(id)
        val battle = battleService.get(session)
        engine
        return Workflow.create(session, battle)
    }

    private fun getSession(id: String): GameSession {
        return sessionService.findByKey(id) ?: throw WorkflowInvalidId(id)
    }

    fun getUnits(p: BattleParticipant): List<GameUnit> {
        return p.units.map { engine.getUnit(it.entity) }
    }

    fun getParticipantsInfo(id: String): List<WorkflowParticipantInfo> {
        val session = sessionService.findByKey(id) ?: throw WorkflowInvalidId(id)
        return session.participants.map { player ->
            val entities = sessionService.getAssignedEntities(session, player)
            val units = entities.map { engine.getUnit(it) }
            val technologies = engine.getPlayer(player.entity).technologies
            WorkflowParticipantInfo(player, units, technologies)
        }
    }

    fun getBattleInfo(id: String): Battle? {
        val session = sessionService.findByKey(id) ?: throw WorkflowInvalidId(id)
        return battleService.get(session)
    }

    fun getSessionsByPlayer(playerId: Long): List<Workflow> {
        return sessionService.findSessionsByPlayer(playerId).map { session ->
            Workflow.create(session, battleService.get(session))
        }
    }

}