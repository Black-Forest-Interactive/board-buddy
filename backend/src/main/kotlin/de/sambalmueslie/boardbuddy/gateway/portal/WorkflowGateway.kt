package de.sambalmueslie.boardbuddy.gateway.portal

import de.sambalmueslie.boardbuddy.gateway.portal.api.PortalBattle
import de.sambalmueslie.boardbuddy.gateway.portal.api.PortalBattleOpponent
import de.sambalmueslie.boardbuddy.workflow.WorkflowService
import de.sambalmueslie.boardbuddy.workflow.api.*
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class WorkflowGateway(
    private val service: WorkflowService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(WorkflowGateway::class.java)
    }

    fun get(id: String) = service.get(id)
    fun getAvailableNations(id: String) = service.getAvailableNations(id)
    fun getTechnologyStatus(id: String, playerId: Long) = service.getTechnologyStatus(id, playerId)
    fun create(request: WorkflowCreateRequest) = service.create(request)
    fun join(id: String, request: WorkflowPlayerJoinRequest) = service.join(id, request)
    fun createUnit(id: String, request: WorkflowCreateUnitRequest) = service.createUnit(id, request)
    fun research(id: String, request: WorkflowResearchRequest) = service.research(id, request)
    fun battleStart(id: String, request: WorkflowBattleStartRequest) = service.battleStart(id, request)
    fun battleFinish(id: String) = service.battleFinish(id)
    fun getParticipantsInfo(id: String) = service.getParticipantsInfo(id)

    fun getMyInfo(id: String, playerId: Long) =
        service.getParticipantsInfo(id).find { it.player.id == playerId }

    fun getPortalBattle(id: String, playerId: Long): PortalBattle? {
        val battle = service.getBattleInfo(id) ?: return null
        return convertToPortalBattle(battle, playerId)
    }

    fun battleCreateFront(id: String, request: WorkflowBattleCreateFrontRequest, playerId: Long): PortalBattle {
        service.battleCreateFront(id, request)
        val battle = service.getBattleInfo(id) ?: throw IllegalStateException("No active battle after createFront")
        return convertToPortalBattle(battle, playerId)
    }

    fun battleAttackFront(id: String, request: WorkflowBattleAttackFrontRequest, playerId: Long): PortalBattle {
        val battle = service.battleAttackFront(id, request)
        return convertToPortalBattle(battle, playerId)
    }

    private fun convertToPortalBattle(battle: Battle, playerId: Long): PortalBattle {
        val myParticipant = battle.participant.find { it.player.player.id == playerId }
            ?: battle.participant.first()
        val opponentParticipant = battle.participant.find { it.player.player.id != playerId }
            ?: battle.participant.last()

        val deployedByOpponent = battle.fronts.flatMap { f ->
            f.units.filter { it.player.player.id == opponentParticipant.player.player.id }.map { it.unit.entity }
        }.toSet()
        val handCount = opponentParticipant.units.count { !deployedByOpponent.contains(it.entity) }

        val opponentInfo = PortalBattleOpponent(
            player = opponentParticipant.player,
            armyCount = opponentParticipant.armyCount,
            handCount = handCount,
        )

        return PortalBattle(
            status = battle.status,
            activePlayer = battle.activePlayer,
            myInfo = myParticipant,
            opponentInfo = opponentInfo,
            fronts = battle.fronts,
            logEntries = battle.logEntries,
            winner = battle.winner,
        )
    }
}
