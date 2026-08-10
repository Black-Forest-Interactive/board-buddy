package de.sambalmueslie.boardbuddy.workflow.battle

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import de.sambalmueslie.boardbuddy.core.player.PlayerService
import de.sambalmueslie.boardbuddy.core.player.api.PlayerType
import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.workflow.api.WorkflowBattleInvalidPlayer
import de.sambalmueslie.boardbuddy.workflow.api.WorkflowBattleNotExisting
import de.sambalmueslie.boardbuddy.workflow.battle.action.BattleActionFactory
import de.sambalmueslie.boardbuddy.workflow.battle.api.*
import de.sambalmueslie.boardbuddy.workflow.battle.cmd.*
import de.sambalmueslie.boardbuddy.workflow.battle.db.BattleData
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

@Singleton
class WorkflowBattleService(
    private val playerService: PlayerService,

    private val actionFactory: BattleActionFactory,
    private val aiPlayer: AiPlayer,

    private val converter: BattleConverter
) : WorkflowBattleAPI {

    companion object {
        private val logger = LoggerFactory.getLogger(WorkflowBattleService::class.java)
    }


    private val activeBattles: Cache<String, BattleData> = Caffeine.newBuilder()
        .expireAfterWrite(12, TimeUnit.HOURS)
        .build()

    override fun get(session: GameSession): Battle? {
        return getData(session)?.let { converter.convert(it) }
    }

    private fun getData(session: GameSession): BattleData? {
        return activeBattles.getIfPresent(session.key)
    }


    override fun start(session: GameSession, request: WorkflowBattleStartRequest): Battle {
        val attacker = getAndValidatePlayer(session, request.attacker.id)
        val defender = getAndValidatePlayer(session, request.defender.id)

        val battle = actionFactory.execute(BattleCmdStart(session, request, attacker, defender))
        activeBattles.put(session.key, battle)
        considerAiMove(session, battle)
        return converter.convert(battle)
    }


    override fun cancel(session: GameSession): Battle {
        val battle = actionFactory.execute(BattleCmdCancel(session, session.getBattle()))
        return converter.convert(battle)
    }

    override fun finish(session: GameSession) {
        actionFactory.execute(BattleCmdFinish(session, session.getBattle()))
        activeBattles.invalidate(session.key)
    }

    fun cleanup(sessionKey: String) {
        activeBattles.invalidate(sessionKey)
    }


    override fun createFront(session: GameSession, request: WorkflowBattleCreateFrontRequest): Battle {
        val player = getAndValidatePlayer(session, request.playerId)
        val battle = actionFactory.execute(BattleCmdFrontCreate(session, session.getBattle(), request, player))
        considerAiMove(session, battle)
        return converter.convert(battle)
    }

    override fun attackFront(session: GameSession, request: WorkflowBattleAttackFrontRequest): Battle {
        val attacker = getAndValidatePlayer(session, request.attackerId)
        val defender = getAndValidatePlayer(session, request.defenderId)

        val battle = actionFactory.execute(BattleCmdFrontAttack(session, session.getBattle(), request, attacker, defender))
        considerAiMove(session, battle)
        return converter.convert(battle)
    }

    private fun GameSession.getBattle(): BattleData {
        return getData(this) ?: throw WorkflowBattleNotExisting(this.key)
    }

    private fun getAndValidatePlayer(session: GameSession, playerId: Long): GameSessionPlayer {
        val player = playerService.get(playerId) ?: throw WorkflowBattleInvalidPlayer(playerId)
        val participant = session.participants.find { it.player.id == player.id } ?: throw WorkflowBattleInvalidPlayer(player.id)
        return participant
    }

    private fun considerAiMove(session: GameSession, battle: BattleData) {
        while (battle.activePlayer.player.type == PlayerType.AI) {
            val cmd = aiPlayer.play(session, battle) ?: break
            actionFactory.execute(cmd)
        }
    }

}