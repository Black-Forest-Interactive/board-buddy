package de.sambalmueslie.boardbuddy.gateway.portal

import de.sambalmueslie.boardbuddy.gateway.portal.api.PortalBattleAttackFrontRequest
import de.sambalmueslie.boardbuddy.gateway.portal.api.PortalBattleCreateFrontRequest
import de.sambalmueslie.boardbuddy.gateway.portal.api.PortalCreateUnitRequest
import de.sambalmueslie.boardbuddy.gateway.portal.api.PortalResearchRequest
import de.sambalmueslie.boardbuddy.gateway.portal.security.PortalPlayerAuthenticationFetcher
import de.sambalmueslie.boardbuddy.gateway.portal.security.playerId
import de.sambalmueslie.boardbuddy.workflow.battle.api.WorkflowBattleStartRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.SecurityRule
import io.swagger.v3.oas.annotations.tags.Tag

@Secured(PortalPlayerAuthenticationFetcher.ROLE)
@Controller(value = "/api/portal/workflow")
@Tag(name = "Portal Workflow API")
class WorkflowController(private val gateway: WorkflowGateway) {

    @Get("{id}")
    fun get(authentication: Authentication, id: String) = gateway.get(id, authentication.playerId())

    /** Link preview for messengers, must stay public. */
    @Secured(SecurityRule.IS_ANONYMOUS)
    @Get("{id}/og-preview")
    @Produces(MediaType.TEXT_HTML)
    fun getOgPreview(id: String): HttpResponse<String> = gateway.getOgPreview(id)

    /** Needed on the join page, possibly before a player exists. Only reveals the free nations of a known session key. */
    @Secured(SecurityRule.IS_ANONYMOUS)
    @Get("{id}/nations")
    fun getAvailableNations(id: String) = gateway.getAvailableNations(id)

    @Get("{id}/qr-code")
    @Produces(MediaType.IMAGE_PNG)
    fun getJoinQrCode(authentication: Authentication, id: String): ByteArray = gateway.getJoinQrCode(id, authentication.playerId())

    @Get("{id}/technology-status")
    fun getTechnologyStatus(authentication: Authentication, id: String) =
        gateway.getTechnologyStatus(id, authentication.playerId())

    @Post("{id}/unit")
    fun createUnit(authentication: Authentication, id: String, @Body request: PortalCreateUnitRequest) =
        gateway.createUnit(id, request, authentication.playerId())

    @Post("{id}/research")
    fun research(authentication: Authentication, id: String, @Body request: PortalResearchRequest) =
        gateway.research(id, request, authentication.playerId())

    @Post("{id}/battle/start")
    fun battleStart(authentication: Authentication, id: String, @Body request: WorkflowBattleStartRequest) =
        gateway.battleStart(id, request, authentication.playerId())

    @Post("{id}/battle/cancel")
    fun battleCancel(authentication: Authentication, id: String) = gateway.battleCancel(id, authentication.playerId())

    @Post("{id}/battle/front")
    fun battleCreateFront(authentication: Authentication, id: String, @Body request: PortalBattleCreateFrontRequest) =
        gateway.battleCreateFront(id, request, authentication.playerId())

    @Post("{id}/battle/attack")
    fun battleAttackFront(authentication: Authentication, id: String, @Body request: PortalBattleAttackFrontRequest) =
        gateway.battleAttackFront(id, request, authentication.playerId())

    @Post("{id}/battle/finish")
    fun battleFinish(authentication: Authentication, id: String) = gateway.battleFinish(id, authentication.playerId())

    @Get("{id}/participants")
    fun getParticipantsInfo(authentication: Authentication, id: String) =
        gateway.getParticipantsInfo(id, authentication.playerId())

    @Get("{id}/my-info")
    fun getMyInfo(authentication: Authentication, id: String) = gateway.getMyInfo(id, authentication.playerId())

    @Get("{id}/battle")
    fun getBattleInfo(authentication: Authentication, id: String) = gateway.getPortalBattle(id, authentication.playerId())
}
