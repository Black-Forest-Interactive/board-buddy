package de.sambalmueslie.boardbuddy.workflow

import de.sambalmueslie.boardbuddy.core.player.PlayerService
import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.core.session.GameSessionService
import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.workflow.api.*
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class WorkflowBattleService(
    private val playerService: PlayerService,
    private val sessionService: GameSessionService,
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
        val unit = participant.getAndValidateUnitInstance(request.unitInstanceId)

        val index = request.index


        TODO("Not yet implemented")
    }


    fun createFront(session: GameSession, request: WorkflowBattleCreateFrontRequest): Battle {
        val player = getAndValidatePlayer(session, request.playerId)
        val battle = getData(session) ?: throw WorkflowBattleNotExisting(session.key)
        battle.validatePlayerIsActive(player)
        val participant = battle.getAndValidateParticipant(player)
        val unit = participant.getAndValidateUnitInstance(request.unitInstanceId)
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

        val attackUnit = attackParticipant.getAndValidateUnitInstance(request.unitInstanceId)
        val attackFront = attackParticipant.createFront(attackUnit, request.frontIndex)

        defendFront.takeHit(attackUnit)

        val isAttackerCounterClass = attackUnit.type.counterType != null && attackUnit.type.counterType == defendUnit.type.unitType
        val defenderStrikesBack = !defendFront.defeated || !isAttackerCounterClass
        if (defenderStrikesBack) attackFront.takeHit(defendUnit)

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
        val units: MutableList<UnitInstance>,
        val fronts: MutableList<BattleFrontData> = mutableListOf()
    ) {
        fun convert() = BattleParticipant(player, units, fronts.map { it.convert() })

        fun getAndValidateUnitInstance(unitInstanceId: Long): UnitInstance {
            return units.find { it.id == unitInstanceId } ?: throw WorkflowBattleUnitNotExisting(unitInstanceId)
        }

        fun createFront(unit: UnitInstance): BattleFrontData {
            val index = (fronts.lastOrNull()?.index ?: 0) + 1
            return createFront(unit, index)
        }

        fun createFront(unit: UnitInstance, index: Int): BattleFrontData {
            if (!units.any { it.id == unit.id }) throw WorkflowBattleUnitNotExisting(unit.id)
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
        val unit: UnitInstance,
        var remainingHealth: Int = unit.health,
        var defeated: Boolean = remainingHealth <= 0,
    ) {
        fun convert() = BattleFront(index, unit, remainingHealth, defeated)

        fun takeHit(unit: UnitInstance) {
            remainingHealth -= unit.health
            defeated = remainingHealth <= 0
        }
    }
}