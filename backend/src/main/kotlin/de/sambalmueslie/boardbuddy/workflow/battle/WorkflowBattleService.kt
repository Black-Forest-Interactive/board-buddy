package de.sambalmueslie.boardbuddy.workflow.battle

import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.player.PlayerService
import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.core.session.api.GameSessionPlayer
import de.sambalmueslie.boardbuddy.engine.GameEngine
import de.sambalmueslie.boardbuddy.engine.api.GameEntity
import de.sambalmueslie.boardbuddy.workflow.api.*
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class WorkflowBattleService(
    private val playerService: PlayerService,

    private val actionStart: BattleActionStart,
    private val actionFrontCreate: BattleActionFrontCreate,
    private val actionFrontAttack: BattleActionFrontAttack,

    private val converter: BattleConverter,
    private val gameEngine: GameEngine,
    eventService: EventService,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(WorkflowBattleService::class.java)
    }

    private val sender = eventService.createSender(GameEntity::class)
    private val activeBattles = mutableMapOf<String, BattleData>()

    fun get(session: GameSession): Battle? {
        return getData(session)?.let { converter.convert(it) }
    }

    private fun getData(session: GameSession): BattleData? {
        return activeBattles[session.key]
    }


    fun start(session: GameSession, request: WorkflowBattleStartRequest): Battle {
        val attacker = getAndValidatePlayer(session, request.attacker.id)
        val defender = getAndValidatePlayer(session, request.defender.id)

        val battle = actionStart.process(session, request, attacker, defender)
        activeBattles[session.key] = battle
        return converter.convert(battle)
    }

    fun finish(session: GameSession) {
        val battle = getData(session) ?: throw WorkflowBattleNotExisting(session.key)
        battle.fronts.flatMap { it.units }
            .filter { it.currentHealth <= 0 }
            .forEach { u ->
                sender.deleted(u.unit)
                gameEngine.delete(u.unit)
            }

        activeBattles.remove(session.key)
    }

    fun createFront(session: GameSession, request: WorkflowBattleCreateFrontRequest): Battle {
        val player = getAndValidatePlayer(session, request.playerId)
        return battleAction(session, player) {
            actionFrontCreate.process( it, request, player)
        }
    }

    fun attackFront(session: GameSession, request: WorkflowBattleAttackFrontRequest): Battle {
        val attacker = getAndValidatePlayer(session, request.attackerId)
        val defender = getAndValidatePlayer(session, request.defenderId)

        return battleAction(session, attacker) {
            actionFrontAttack.process( it, request, attacker, defender)
        }

    }

    private fun battleAction(session: GameSession, player: GameSessionPlayer, action: (BattleData) -> BattleData): Battle {
        val battle = getData(session) ?: throw WorkflowBattleNotExisting(session.key)
        battle.validatePlayerIsActive(player)

        val result = action.invoke(battle)

        updateStatus(battle)

        if (result.status != BattleStatus.FINISHED) {
            switchActivePlayer(result, player)
        }

        return converter.convert(result)
    }

    private fun switchActivePlayer(data: BattleData, player: GameSessionPlayer) {
        val currentIndex = data.participant.indexOfFirst { it.matches(player) }
        val nextIndex = if (currentIndex >= data.participant.size - 1) 0 else currentIndex + 1
        val nextPlayer = data.participant[nextIndex].player
        data.activePlayer = nextPlayer
    }

    private fun getAndValidatePlayer(session: GameSession, playerId: Long): GameSessionPlayer {
        val player = playerService.get(playerId) ?: throw WorkflowBattleInvalidPlayer(playerId)
        val participant = session.participants.find { it.player.id == player.id } ?: throw WorkflowBattleInvalidPlayer(player.id)
        return participant
    }

    private fun updateStatus(battle: BattleData) {
        val noUnitsAvailable = battle.participant.all { it.units.isEmpty() }
        battle.status = if (noUnitsAvailable) BattleStatus.FINISHED else BattleStatus.ONGOING
        if (battle.status == BattleStatus.FINISHED) {
            val playerRemainingHealth = battle.fronts.flatMap { it.units }.groupBy { it.player }
                .mapValues { it.value.sumOf { u -> u.currentHealth } }
                .filter { it.value > 0 }
            battle.winner = playerRemainingHealth.maxByOrNull { it.value }?.key
        }
    }

}