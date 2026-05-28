package de.sambalmueslie.boardbuddy.workflow

import de.sambalmueslie.boardbuddy.core.game.GameService
import de.sambalmueslie.boardbuddy.core.nation.api.Nation
import de.sambalmueslie.boardbuddy.core.ruleset.RuleSetService
import de.sambalmueslie.boardbuddy.core.session.GameSessionService
import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.core.session.api.GameSessionChangeRequest
import de.sambalmueslie.boardbuddy.engine.GameEngine
import de.sambalmueslie.boardbuddy.engine.api.GameUnit
import de.sambalmueslie.boardbuddy.workflow.api.*
import de.sambalmueslie.boardbuddy.workflow.battle.WorkflowBattleService
import de.sambalmueslie.boardbuddy.workflow.sse.SessionEventService
import de.sambalmueslie.boardbuddy.workflow.sse.SessionEventType
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
    private val nationService: WorkflowNationService,

    private val sessionService: GameSessionService,
    private val engine: GameEngine,
    private val eventService: SessionEventService,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(WorkflowService::class.java)
    }


    fun create(request: WorkflowCreateRequest): Workflow {
        val game = gameService.get(request.gameId) ?: throw WorkflowInvalidGame(request.gameId)
        val ruleSet = ruleSetService.get(request.ruleSetId) ?: throw WorkflowInvalidRuleSet(request.ruleSetId)
        val host = playerService.createHost(request.hostId, request.nationId, ruleSet.unitDefinitions)
        val session = sessionService.create(GameSessionChangeRequest(request.name, host.player, host.entity, game, ruleSet))
        return Workflow.create(session, null)
    }

    fun join(id: String, request: WorkflowPlayerJoinRequest): Workflow {
        val session = getSession(id)
        playerService.join(session, request)
        return get(id)
    }

    fun assign(id: String, request: WorkflowAssignPlayerRequest): Workflow {
        val session = getSession(id)
        playerService.assign(session, request)
        return get(id)
    }

    fun createUnit(id: String, request: WorkflowCreateUnitRequest): Workflow {
        val session = getSession(id)
        unitService.createUnit(session, request)
        return get(id)
    }

    fun battleStart(id: String, request: WorkflowBattleStartRequest): Workflow {
        val session = getSession(id)
        battleService.start(session, request)
        eventService.emit(id, SessionEventType.BATTLE_STARTED)
        return get(id)
    }

    fun battleCancel(id: String): Workflow {
        val session = getSession(id)
        battleService.cancel(session)
        eventService.emit(id, SessionEventType.BATTLE_CANCELLED)
        return get(id)
    }

    fun battleCreateFront(id: String, request: WorkflowBattleCreateFrontRequest): Workflow {
        val session = getSession(id)
        battleService.createFront(session, request)
        eventService.emit(id, SessionEventType.BATTLE_FRONT_CREATED)
        return get(id)
    }

    fun battleAttackFront(id: String, request: WorkflowBattleAttackFrontRequest): Battle {
        val session = getSession(id)
        val result = battleService.attackFront(session, request)
        eventService.emit(id, SessionEventType.BATTLE_FRONT_ATTACKED)
        return result
    }

    fun battleFinish(id: String) {
        val session = getSession(id)
        battleService.finish(session)
        eventService.emit(id, SessionEventType.BATTLE_FINISHED)
        eventService.cleanup(id)
    }

    fun research(id: String, request: WorkflowResearchRequest): Workflow {
        val session = getSession(id)
        researchService.research(session, request)
        return get(id)
    }

    fun get(id: String): Workflow {
        val session = sessionService.findByKey(id) ?: throw WorkflowInvalidId(id)
        val battle = battleService.get(session)
        return Workflow.create(session, battle)
    }

    fun getAvailableNations(id: String): Set<Nation> {
        val session = getSession(id)
        return nationService.getAvailableNations(session)
    }

    fun getTechnologyStatus(id: String, playerId: Long): TechnologyStatus {
        val session = getSession(id)
        return researchService.getTechnologyStatus(session, playerId)
    }


    private fun getSession(id: String): GameSession {
        return sessionService.findByKey(id) ?: throw WorkflowInvalidId(id)
    }

    fun getUnits(p: BattleParticipant): List<GameUnit> {
        return p.units.map { engine.getUnit(it.entity) }
    }

    fun getParticipantsInfo(id: String): List<WorkflowParticipantInfo> {
        val session = sessionService.findByKey(id) ?: throw WorkflowInvalidId(id)
        val available = session.ruleSet.technologies
        return session.participants.map { p ->
            val entities = sessionService.getAssignedEntities(session, p)
            val units = entities.map { engine.getUnit(it) }
            val player = engine.getPlayer(p.entity)
            val unitLevel = player.unitProgress?.entries ?: emptyMap()
            WorkflowParticipantInfo(p.player, player.nation, player.government, units, unitLevel, player.technologies, available)
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