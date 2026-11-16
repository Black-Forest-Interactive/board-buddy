package de.sambalmueslie.boardbuddy.gateway.portal

import de.sambalmueslie.boardbuddy.gateway.portal.api.PortalBattle
import de.sambalmueslie.boardbuddy.gateway.portal.api.PortalBattleAttackFrontRequest
import de.sambalmueslie.boardbuddy.gateway.portal.api.PortalBattleCreateFrontRequest
import de.sambalmueslie.boardbuddy.gateway.portal.api.PortalBattleOpponent
import de.sambalmueslie.boardbuddy.gateway.portal.api.PortalCreateUnitRequest
import de.sambalmueslie.boardbuddy.gateway.portal.api.PortalResearchRequest
import de.sambalmueslie.boardbuddy.infrastructure.ProtocolService
import de.sambalmueslie.boardbuddy.workflow.WorkflowService
import de.sambalmueslie.boardbuddy.workflow.api.TechnologyStatus
import de.sambalmueslie.boardbuddy.workflow.api.Workflow
import de.sambalmueslie.boardbuddy.workflow.api.WorkflowCreateUnitRequest
import de.sambalmueslie.boardbuddy.workflow.api.WorkflowBattleNotExisting
import de.sambalmueslie.boardbuddy.workflow.api.WorkflowParticipantInfo
import de.sambalmueslie.boardbuddy.workflow.api.WorkflowPlayerActionForbidden
import de.sambalmueslie.boardbuddy.workflow.api.WorkflowResearchRequest
import de.sambalmueslie.boardbuddy.workflow.battle.api.Battle
import de.sambalmueslie.boardbuddy.workflow.battle.api.WorkflowBattleAttackFrontRequest
import de.sambalmueslie.boardbuddy.workflow.battle.api.WorkflowBattleCreateFrontRequest
import de.sambalmueslie.boardbuddy.workflow.battle.api.WorkflowBattleStartRequest
import de.sambalmueslie.boardbuddy.workflow.sse.SessionEvent
import de.sambalmueslie.boardbuddy.workflow.sse.SessionEventService
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import jakarta.inject.Singleton
import reactor.core.publisher.Flux
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Portal facing workflow operations.
 * Every operation receives the authenticated player id and checks session membership first.
 */
@Singleton
class WorkflowGateway(
    private val service: WorkflowService,
    private val eventService: SessionEventService,
    protocolService: ProtocolService
) {

    private val protocol = protocolService.getProtocol("portal", "Workflow")

    fun get(id: String, playerId: Long): Workflow = requireParticipant(id, playerId)

    fun getOgPreview(id: String): HttpResponse<String> {
        protocol.log("[$id] get og preview")
        val workflow = runCatching { service.get(id) }.getOrNull()
            ?: return HttpResponse.notFound()
        val baseUrl = "https://blackforrestdevelopment.de"
        val encodedId = URLEncoder.encode(id, StandardCharsets.UTF_8)
        val joinUrl = "$baseUrl/session/join?key=$encodedId"
        val name = htmlEscape(workflow.name)
        val hostName = htmlEscape(workflow.host.name)
        val gameName = htmlEscape(workflow.game.name)
        val participantCount = workflow.participants.size
        val description = "$hostName invites you to join \"$name\" " +
                "($gameName) — $participantCount player${if (participantCount == 1) "" else "s"} already in."
        val content = """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="utf-8">
              <title>$name — Board Buddy</title>
              <meta property="og:type" content="website">
              <meta property="og:title" content="$name — Board Buddy">
              <meta property="og:description" content="$description">
              <meta property="og:image" content="$baseUrl/img/og-preview.png">
              <meta property="og:url" content="$baseUrl/api/portal/workflow/$encodedId/og-preview">
              <meta name="twitter:card" content="summary">
              <meta name="twitter:title" content="$name — Board Buddy">
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

    private fun htmlEscape(value: String): String = value
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;")

    fun getAvailableNations(id: String) = service.getAvailableNations(id)

    fun getTechnologyStatus(id: String, playerId: Long): TechnologyStatus {
        requireParticipant(id, playerId)
        return service.getTechnologyStatus(id, playerId)
    }

    fun createUnit(id: String, request: PortalCreateUnitRequest, playerId: Long): Workflow {
        requireParticipant(id, playerId)
        return protocol.log("[$id] $playerId create unit ${request.unitTypeId}", request) {
            service.createUnit(id, WorkflowCreateUnitRequest(playerId, request.unitTypeId))
        }
    }

    fun research(id: String, request: PortalResearchRequest, playerId: Long): Workflow {
        requireParticipant(id, playerId)
        return protocol.log("[$id] $playerId research ${request.technologyId}", request) {
            service.research(id, WorkflowResearchRequest(playerId, request.technologyId))
        }
    }

    fun battleStart(id: String, request: WorkflowBattleStartRequest, playerId: Long): Workflow {
        val workflow = requireParticipant(id, playerId)
        val allowed = setOf(request.attacker.id, request.defender.id, workflow.host.id)
        if (playerId !in allowed) throw WorkflowPlayerActionForbidden(playerId)
        return protocol.log("[$id] battle start ${request.attacker.id} vs ${request.defender.id}", request) { service.battleStart(id, request) }
    }

    fun battleCancel(id: String, playerId: Long) = protocol.log("[$id] battle cancel") {
        checkBattleAccess(id, playerId)
        service.battleCancel(id)
    }

    fun battleFinish(id: String, playerId: Long) = protocol.log("[$id] battle finish") {
        checkBattleAccess(id, playerId)
        service.battleFinish(id)
    }

    private fun checkBattleAccess(id: String, playerId: Long) {
        val workflow = requireParticipant(id, playerId)
        val inBattle = workflow.activeBattle?.participant?.any { it.player.player.id == playerId } ?: false
        if (!inBattle && workflow.host.id != playerId) throw WorkflowPlayerActionForbidden(playerId)
    }

    fun getParticipantsInfo(id: String, playerId: Long): List<WorkflowParticipantInfo> {
        requireParticipant(id, playerId)
        return service.getParticipantsInfo(id)
    }

    fun getMyInfo(id: String, playerId: Long) =
        getParticipantsInfo(id, playerId).find { it.player.id == playerId }

    fun getPortalBattle(id: String, playerId: Long): PortalBattle? {
        requireParticipant(id, playerId)
        val battle = service.getBattleInfo(id) ?: return null
        return convertToPortalBattle(battle, playerId)
    }

    fun battleCreateFront(id: String, request: PortalBattleCreateFrontRequest, playerId: Long): PortalBattle {
        requireParticipant(id, playerId)
        protocol.log("[$id] battle $playerId create front ${request.entityId}", request)
        service.battleCreateFront(id, WorkflowBattleCreateFrontRequest(playerId, request.entityId))
        val battle = service.getBattleInfo(id) ?: throw IllegalStateException("No active battle after createFront")
        return convertToPortalBattle(battle, playerId)
    }

    fun battleAttackFront(id: String, request: PortalBattleAttackFrontRequest, playerId: Long): PortalBattle {
        requireParticipant(id, playerId)
        val current = service.getBattleInfo(id) ?: throw WorkflowBattleNotExisting(id)
        if (current.participant.none { it.player.player.id == playerId }) throw WorkflowPlayerActionForbidden(playerId)
        val defenderId = current.participant.first { it.player.player.id != playerId }.player.player.id

        protocol.log("[$id] battle $playerId attack front ${request.frontIndex}", request)
        val battle = service.battleAttackFront(id, WorkflowBattleAttackFrontRequest(playerId, defenderId, request.entityId, request.frontIndex))
        return convertToPortalBattle(battle, playerId)
    }

    fun subscribeEvents(id: String, playerId: Long): Flux<SessionEvent> {
        requireParticipant(id, playerId)
        return eventService.subscribe(id)
    }

    private fun requireParticipant(id: String, playerId: Long): Workflow {
        val workflow = service.get(id)
        if (workflow.participants.none { it.player.id == playerId }) throw WorkflowPlayerActionForbidden(playerId)
        return workflow
    }

    private fun convertToPortalBattle(battle: Battle, playerId: Long): PortalBattle {
        val own = battle.participant.find { it.player.player.id == playerId }
        val spectator = own == null
        val myParticipant = (own ?: battle.participant.first()).let { if (spectator) it.copy(units = emptyList()) else it }
        val opponentParticipant = if (spectator) battle.participant.last()
        else battle.participant.first { it.player.player.id != playerId }

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
            spectator = spectator,
        )
    }
}
