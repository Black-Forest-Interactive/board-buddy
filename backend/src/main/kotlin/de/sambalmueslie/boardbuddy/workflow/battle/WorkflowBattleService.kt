package de.sambalmueslie.boardbuddy.workflow.battle

import de.sambalmueslie.boardbuddy.core.player.PlayerService
import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.workflow.api.*
import de.sambalmueslie.boardbuddy.workflow.battle.action.BattleActionFactory
import de.sambalmueslie.boardbuddy.workflow.battle.action.BattleActionFrontAttack
import de.sambalmueslie.boardbuddy.workflow.battle.action.BattleActionFrontCreate
import de.sambalmueslie.boardbuddy.workflow.battle.cmd.*
import de.sambalmueslie.boardbuddy.workflow.battle.db.BattleData
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class WorkflowBattleService(
    private val playerService: PlayerService,

    private val actionFrontCreate: BattleActionFrontCreate,
    private val actionFrontAttack: BattleActionFrontAttack,

    private val actionFactory: BattleActionFactory,

    private val converter: BattleConverter,
) : WorkflowBattleAPI {

    companion object {
        private val logger = LoggerFactory.getLogger(WorkflowBattleService::class.java)
    }


    private val activeBattles = mutableMapOf<String, BattleData>()

    override fun get(session: GameSession): Battle? {
        return getData(session)?.let { converter.convert(it) }
    }

    private fun getData(session: GameSession): BattleData? {
        return activeBattles[session.key]
    }


    override fun start(session: GameSession, request: WorkflowBattleStartRequest): Battle {
        val attacker = getAndValidatePlayer(session, request.attacker.id)
        val defender = getAndValidatePlayer(session, request.defender.id)
        val battle = actionFactory.execute(BattleCmdStart(session, request, attacker, defender))
        activeBattles[session.key] = battle
        return converter.convert(battle)
    }

    override fun cancel(session: GameSession): Battle {
        val battle = actionFactory.execute(BattleCmdCancel(session, session.getBattle()))
        return converter.convert(battle)
    }

    override fun finish(session: GameSession) {
        val battle = actionFactory.execute(BattleCmdFinish(session, session.getBattle()))
        activeBattles.remove(session.key)
    }


    override fun createFront(session: GameSession, request: WorkflowBattleCreateFrontRequest): Battle {
        val player = getAndValidatePlayer(session, request.playerId)
        val battle = actionFactory.execute(BattleCmdFrontCreate(session, session.getBattle(), request, player))
        return converter.convert(battle)
    }

    override fun attackFront(session: GameSession, request: WorkflowBattleAttackFrontRequest): Battle {
        val attacker = getAndValidatePlayer(session, request.attackerId)
        val defender = getAndValidatePlayer(session, request.defenderId)

        val battle = actionFactory.execute(BattleCmdFrontAttack(session, session.getBattle(), request, attacker, defender))
        return converter.convert(battle)
    }

    private fun GameSession.getBattle() : BattleData {
        return  getData(this) ?: throw WorkflowBattleNotExisting(this.key)
    }

    private fun getAndValidatePlayer(session: GameSession, playerId: Long): GameSessionPlayer {
        val player = playerService.get(playerId) ?: throw WorkflowBattleInvalidPlayer(playerId)
        val participant = session.participants.find { it.player.id == player.id } ?: throw WorkflowBattleInvalidPlayer(player.id)
        return participant
    }

}