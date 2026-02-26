package de.sambalmueslie.boardbuddy.workflow

import de.sambalmueslie.boardbuddy.core.player.PlayerService
import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.core.session.GameSessionService
import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.engine.GameEngine
import de.sambalmueslie.boardbuddy.engine.api.GameEntity
import de.sambalmueslie.boardbuddy.workflow.api.*
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class WorkflowBattleService(
    private val playerService: PlayerService,
    private val sessionService: GameSessionService,
    private val gameEngine: GameEngine,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(WorkflowBattleService::class.java)
        private val UNITS_PER_BATTLE = 3
    }

    private val activeBattles = mutableMapOf<String, BattleData>()

    fun start(session: GameSession, request: WorkflowBattleStartRequest): Battle {
        val attacker = getAndValidatePlayer(session, request.attackerId)
        val defender = getAndValidatePlayer(session, request.defenderId)
        val attackerUnits = sessionService.getAssignedUnits(session, attacker).shuffled().take(UNITS_PER_BATTLE).toMutableList()
        val defenderUnits = sessionService.getAssignedUnits(session, defender).shuffled().take(UNITS_PER_BATTLE).toMutableList()
        val battle = BattleData(listOf(BattleParticipantData(attacker, attackerUnits), BattleParticipantData(defender, defenderUnits)), attacker)
        activeBattles[session.key] = battle
        return battle.convert()
    }

    fun get(session: GameSession): Battle? {
        return getData(session)?.convert()
    }

    private fun getData(session: GameSession): BattleData? {
        return activeBattles[session.key]
    }

    private fun getAndValidatePlayer(session: GameSession, playerId: Long): Player {
        val player = playerService.get(playerId) ?: throw WorkflowBattleInvalidPlayer(playerId)
        if (!session.participants.any { it.id == playerId }) throw WorkflowBattleInvalidPlayer(playerId)
        return player
    }


    fun addUnit(session: GameSession, request: WorkflowBattleAddUnitRequest) {
        val player = getAndValidatePlayer(session, request.playerId)
        val battle = getData(session) ?: throw WorkflowBattleNotExisting(session.key)
        battle.validatePlayerIsActive(player)

        val participant = battle.getAndValidateParticipant(player)
        val unit = participant.getAndValidateUnitEntity(request.unitInstanceId)

        val index = request.index


        TODO("Not yet implemented")
    }


    fun createFront(session: GameSession, request: WorkflowBattleCreateFrontRequest): Battle {
        val player = getAndValidatePlayer(session, request.playerId)
        val battle = getData(session) ?: throw WorkflowBattleNotExisting(session.key)
        battle.validatePlayerIsActive(player)
        val participant = battle.getAndValidateParticipant(player)
        val unit = participant.getAndValidateUnitEntity(request.unitInstanceId)
        participant.createFront(unit)

        battle.switchActivePlayer(player)
        return battle.convert()
    }

    fun attackFront(session: GameSession, request: WorkflowBattleAttackFrontRequest): Battle {
        val attacker = getAndValidatePlayer(session, request.attackerId)
        val defender = getAndValidatePlayer(session, request.defenderId)

        val battle = getData(session) ?: throw WorkflowBattleNotExisting(session.key)
        battle.validatePlayerIsActive(attacker)
        val attackParticipant = battle.getAndValidateParticipant(attacker)
        val defendParticipant = battle.getAndValidateParticipant(defender)


        val defendFront = defendParticipant.fronts.find { it.index == request.frontIndex } ?: throw WorkflowBattleInvalidFrontIndex(request.frontIndex)
        val defendUnit = defendFront.unit

        val attackUnit = attackParticipant.getAndValidateUnitEntity(request.unitInstanceId)

        gameEngine.combat(attackUnit, defendUnit)

        battle.switchActivePlayer(attacker)
        return battle.convert()
    }


    private data class BattleData(
        val participant: List<BattleParticipantData>,
        var activePlayer: Player,
    ) {
        fun convert() = Battle(participant.map { it.convert() }, activePlayer)

        fun getAndValidateParticipant(player: Player): BattleParticipantData {
            return participant.find { it.player.id == player.id } ?: throw WorkflowBattleInvalidPlayer(player.id)
        }

        fun validatePlayerIsActive(player: Player) {
            if (player.id != activePlayer.id) throw WorkflowBattlePlayerIsNotActive(player.id)
        }

        fun switchActivePlayer(player: Player) {
            val currentIndex = participant.indexOfFirst { it.player.id == player.id }
            val nextIndex = if (currentIndex >= participant.size - 1) 0 else currentIndex + 1
            val nextPlayer = participant[nextIndex].player
            activePlayer = nextPlayer
        }
    }

    private data class BattleParticipantData(
        val player: Player,
        val units: MutableList<GameEntity>,
        val fronts: MutableList<BattleFrontData> = mutableListOf()
    ) {
        fun convert() = BattleParticipant(player, units, fronts.map { it.convert() })

        fun getAndValidateUnitEntity(entityId: Long): GameEntity {
            return units.find { it == entityId } ?: throw WorkflowBattleUnitNotExisting(entityId)
        }

        fun createFront(unit: GameEntity): BattleFrontData {
            val index = (fronts.lastOrNull()?.index ?: 0) + 1
            return createFront(unit, index)
        }

        fun createFront(unit: GameEntity, index: Int): BattleFrontData {
            if (!units.any { it == unit }) throw WorkflowBattleUnitNotExisting(unit)
            units.remove(unit)

            val existing = fronts.find { it.index == index }
            if (existing != null) throw WorkflowBattleInvalidFrontIndex(index)

            val front = BattleFrontData(index, unit)
            fronts.add(front)
            return front
        }

    }

    private data class BattleFrontData(
        val index: Int,
        val unit: GameEntity
    ) {
        fun convert() = BattleFront(index, unit)
    }
}