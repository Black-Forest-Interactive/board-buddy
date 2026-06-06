package de.sambalmueslie.boardbuddy.gateway.portal

import de.sambalmueslie.boardbuddy.gateway.portal.api.PortalBattle
import de.sambalmueslie.boardbuddy.gateway.portal.api.PortalBattleOpponent
import de.sambalmueslie.boardbuddy.infrastructure.ProtocolService
import de.sambalmueslie.boardbuddy.workflow.WorkflowService
import de.sambalmueslie.boardbuddy.workflow.api.WorkflowCreateRequest
import de.sambalmueslie.boardbuddy.workflow.api.WorkflowCreateUnitRequest
import de.sambalmueslie.boardbuddy.workflow.api.WorkflowPlayerJoinRequest
import de.sambalmueslie.boardbuddy.workflow.api.WorkflowResearchRequest
import de.sambalmueslie.boardbuddy.workflow.battle.api.Battle
import de.sambalmueslie.boardbuddy.workflow.battle.api.WorkflowBattleAttackFrontRequest
import de.sambalmueslie.boardbuddy.workflow.battle.api.WorkflowBattleCreateFrontRequest
import de.sambalmueslie.boardbuddy.workflow.battle.api.WorkflowBattleStartRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class WorkflowGateway(
    private val service: WorkflowService,
    private val protocolService: ProtocolService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(WorkflowGateway::class.java)
    }

    private val protocol = protocolService.getProtocol("portal", "Workflow")

    fun get(id: String) =  service.get(id)

    fun getOgPreview(id: String): HttpResponse<String> {
        protocol.log("[$id] get og preview")
        val workflow = runCatching { service.get(id) }.getOrNull()
            ?: return HttpResponse.notFound()
        val baseUrl = "https://blackforrestdevelopment.de"
        val joinUrl = "$baseUrl/session/join?key=$id"
        val participantCount = workflow.participants.size
        val description = "${workflow.host.name} invites you to join \"${workflow.name}\" " +
                "(${workflow.game.name}) — $participantCount player${if (participantCount == 1) "" else "s"} already in."
        val content = """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="utf-8">
              <title>${workflow.name} — Board Buddy</title>
              <meta property="og:type" content="website">
              <meta property="og:title" content="${workflow.name} — Board Buddy">
              <meta property="og:description" content="$description">
              <meta property="og:image" content="$baseUrl/img/og-preview.png">
              <meta property="og:url" content="$baseUrl/api/portal/workflow/$id/og-preview">
              <meta name="twitter:card" content="summary">
              <meta name="twitter:title" content="${workflow.name} — Board Buddy">
              <meta name="twitter:description" content="$description">
              <meta http-equiv="refresh" content="0;url=$joinUrl">
            </head>
            <body>
              <script>window.location.href = '$joinUrl'</script>
            </body>
            </html>
        """.trimIndent()
        return HttpResponse.ok(content).contentType(MediaType.TEXT_HTML)
    }

    fun getAvailableNations(id: String) = service.getAvailableNations(id)
    fun getTechnologyStatus(id: String, playerId: Long) = service.getTechnologyStatus(id, playerId)
    fun create(request: WorkflowCreateRequest) = protocol.log("Create ${request.name}", request) { service.create(request) }
    fun join(id: String, request: WorkflowPlayerJoinRequest) = protocol.log("[$id] join ${request.name}", request) { service.join(id, request) }
    fun createUnit(id: String, request: WorkflowCreateUnitRequest) = protocol.log("[$id] ${request.playerId} create unit ${request.unitTypeId}", request) { service.createUnit(id, request) }
    fun research(id: String, request: WorkflowResearchRequest) = protocol.log("[$id] ${request.playerId} research ${request.technologyId}", request) { service.research(id, request) }
    fun battleStart(id: String, request: WorkflowBattleStartRequest) = protocol.log("[$id] battle start ${request.attacker.id} vs ${request.defender.id}", request) { service.battleStart(id, request) }
    fun battleCancel(id: String) = protocol.log("[$id] battle cancel") { service.battleCancel(id) }
    fun battleFinish(id: String) = protocol.log("[$id] battle finish") { service.battleFinish(id) }
    fun getParticipantsInfo(id: String) = service.getParticipantsInfo(id)

    fun getMyInfo(id: String, playerId: Long) =
        service.getParticipantsInfo(id).find { it.player.id == playerId }

    fun getPortalBattle(id: String, playerId: Long): PortalBattle? {
        val battle = service.getBattleInfo(id) ?: return null
        return convertToPortalBattle(battle, playerId)
    }

    fun battleCreateFront(id: String, request: WorkflowBattleCreateFrontRequest, playerId: Long): PortalBattle {
        protocol.log("[$id] battle $playerId create front ${request.entityId}", request)
        service.battleCreateFront(id, request)
        val battle = service.getBattleInfo(id) ?: throw IllegalStateException("No active battle after createFront")
        return convertToPortalBattle(battle, playerId)
    }

    fun battleAttackFront(id: String, request: WorkflowBattleAttackFrontRequest, playerId: Long): PortalBattle {
        protocol.log("[$id] battle $playerId attack front ${request.frontIndex}", request)
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
