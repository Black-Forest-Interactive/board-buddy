package de.sambalmueslie.boardbuddy.core.workflow

import de.sambalmueslie.boardbuddy.core.player.PlayerService
import de.sambalmueslie.boardbuddy.core.player.api.Player
import de.sambalmueslie.boardbuddy.core.session.GameSessionService
import de.sambalmueslie.boardbuddy.core.session.api.GameSession
import de.sambalmueslie.boardbuddy.core.unit.api.UnitInstance
import de.sambalmueslie.boardbuddy.core.workflow.api.*
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

    fun createFront(session: GameSession, request: WorkflowBattleCreateFrontRequest): Battle {
        val player = getAndValidatePlayer(session, request.playerId)
        val battle = getData(session) ?: throw WorkflowBattleNotExisting(session.key)
        val participant = getAndValidateParticipant(battle, player)
        val unit = getAndValidateUnitInstance(participant, request.unitInstanceId)
        participant.units.remove(unit)

        val front = BattleFrontData((participant.fronts.lastOrNull()?.index ?: 0) + 1, unit)
        participant.fronts.add(front)

        switchActivePlayer(battle, player)
        return battle.convert()
    }

    private fun switchActivePlayer(battle: BattleData, player: Player) {
        val currentIndex = battle.participant.indexOfFirst { it.player.id == player.id }
        val nextIndex = if (currentIndex > battle.participant.size - 1) 0 else currentIndex + 1
        val nextPlayer = battle.participant[nextIndex].player
        battle.activePlayer = nextPlayer
    }

    private fun getAndValidateParticipant(battle: BattleData, player: Player): BattleParticipantData {
        val participant = battle.participant.find { it.player.id == player.id } ?: throw WorkflowBattleInvalidPlayer(player.id)
        if (participant.player.id != battle.activePlayer.id) throw WorkflowBattlePlayerIsNotActive(player.id)
        return participant
    }

    private fun getAndValidateUnitInstance(participant: BattleParticipantData, unitInstanceId: Long): UnitInstance {
        return participant.units.find { it.id == unitInstanceId } ?: throw WorkflowBattleUnitNotExisting(unitInstanceId)
    }

    fun attackFront(session: GameSession, request: WorkflowBattleAttackFrontRequest) {
        TODO("Not yet implemented")
    }

    private data class BattleData(
        val participant: List<BattleParticipantData>,
        var activePlayer: Player,
    ) {
        fun convert() = Battle(participant.map { it.convert() }, activePlayer)
    }

    private data class BattleParticipantData(
        val player: Player,
        val units: MutableList<UnitInstance>,
        val fronts: MutableList<BattleFrontData> = mutableListOf()
    ) {
        fun convert() = BattleParticipant(player, units, fronts.map { it.convert() })
    }

    private data class BattleFrontData(
        val index: Int,
        val unit: UnitInstance,
        var remainingHealth: Int = unit.health,
        var defeated: Boolean = remainingHealth <= 0,
    ) {
        fun convert() = BattleFront(index, unit, remainingHealth, defeated)
    }
}