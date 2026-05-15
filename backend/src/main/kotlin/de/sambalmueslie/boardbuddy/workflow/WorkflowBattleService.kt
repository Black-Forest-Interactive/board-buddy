package de.sambalmueslie.boardbuddy.workflow

import de.sambalmueslie.boardbuddy.core.player.PlayerService
import de.sambalmueslie.boardbuddy.core.session.GameSessionService
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
    private val sessionService: GameSessionService,
    private val gameEngine: GameEngine,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(WorkflowBattleService::class.java)
    }

    private val activeBattles = mutableMapOf<String, BattleData>()

    fun start(session: GameSession, request: WorkflowBattleStartRequest): Battle {
        val attacker = getAndValidatePlayer(session, request.attacker.id)
        val defender = getAndValidatePlayer(session, request.defender.id)
        val battleType = request.type

        val attackerUnits = sessionService.getAssignedEntities(session, attacker).let { gameEngine.determineAttackerUnits(attacker,request.attacker.armyCount, battleType, it) }.toMutableList()
        val defenderUnits = sessionService.getAssignedEntities(session, defender).let { gameEngine.determineDefenderUnits(defender,request.defender.armyCount, battleType, it) }.toMutableList()
        val startPlayer = gameEngine.determineStartPlayer(attacker, defender, battleType, request.isWalled)

        val participants = listOf(
            BattleParticipantData(attacker, request.attacker.armyCount, attackerUnits),
            BattleParticipantData(defender, request.defender.armyCount, defenderUnits)
        )
        val battle = BattleData(participants, battleType, startPlayer)
        activeBattles[session.key] = battle
        return battle.convert()
    }

    fun get(session: GameSession): Battle? {
        return getData(session)?.convert()
    }


    fun getInfo(session: GameSession): BattleInfo? {
        return getData(session)?.toInfo(gameEngine)
    }

    private fun getData(session: GameSession): BattleData? {
        return activeBattles[session.key]
    }

    private fun getAndValidatePlayer(session: GameSession, playerId: Long): GameSessionPlayer {
        val player = playerService.get(playerId) ?: throw WorkflowBattleInvalidPlayer(playerId)
        val participant = session.participants.find { it.player.id == playerId } ?: throw WorkflowBattleInvalidPlayer(playerId)
        return participant
    }


    fun addUnit(session: GameSession, request: WorkflowBattleAddUnitRequest) {
        val player = getAndValidatePlayer(session, request.playerId)
        val battle = getData(session) ?: throw WorkflowBattleNotExisting(session.key)
        battle.validatePlayerIsActive(player)

        val participant = battle.getAndValidateParticipant(player)
        val unit = participant.getAndValidateUnitEntity(request.entityId)

        val index = request.index


        TODO("Not yet implemented")
    }


    fun createFront(session: GameSession, request: WorkflowBattleCreateFrontRequest): Battle {
        val player = getAndValidatePlayer(session, request.playerId)
        val battle = getData(session) ?: throw WorkflowBattleNotExisting(session.key)
        battle.validatePlayerIsActive(player)
        val participant = battle.getAndValidateParticipant(player)
        val unit = participant.getAndValidateUnitEntity(request.entityId)
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

        val attackUnit = attackParticipant.getAndValidateUnitEntity(request.entityId)

        gameEngine.combat(attackUnit, defendUnit)

        battle.switchActivePlayer(attacker)
        return battle.convert()
    }


    private data class BattleData(
        val participant: List<BattleParticipantData>,
        val type: BattleType,
        var activePlayer: GameSessionPlayer,
    ) {
        fun convert() = Battle(participant.map { it.convert() }, activePlayer)
        fun toInfo(engine: GameEngine) = BattleInfo(participant.map { it.toInfo(engine) }, activePlayer)

        fun getAndValidateParticipant(player: GameSessionPlayer): BattleParticipantData {
            return participant.find { it.matches(player) } ?: throw WorkflowBattleInvalidPlayer(player.player.id)
        }

        fun validatePlayerIsActive(player: GameSessionPlayer) {
            if (player.player.id != activePlayer.player.id) throw WorkflowBattlePlayerIsNotActive(player.player.id)
        }

        fun switchActivePlayer(player: GameSessionPlayer) {
            val currentIndex = participant.indexOfFirst { it.matches(player) }
            val nextIndex = if (currentIndex >= participant.size - 1) 0 else currentIndex + 1
            val nextPlayer = participant[nextIndex].player
            activePlayer = nextPlayer
        }
    }

    private data class BattleParticipantData(
        val player: GameSessionPlayer,
        val armyCount: Int,
        val units: MutableList<GameEntity>,
        val fronts: MutableList<BattleFrontData> = mutableListOf()
    ) {
        fun convert() = BattleParticipant(player, armyCount, units, fronts.map { it.convert() })

        fun toInfo(engine: GameEngine) = BattleParticipantInfo(player, armyCount, units.map { engine.getUnit(it) }, fronts.map { it.toInfo(engine) })

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

        fun matches(p: GameSessionPlayer) = player.player.id == p.player.id

    }

    private data class BattleFrontData(
        val index: Int,
        val unit: GameEntity
    ) {
        fun convert() = BattleFront(index, unit)
        fun toInfo(engine: GameEngine) = BattleFrontInfo(index, engine.getUnit(unit))
    }
}