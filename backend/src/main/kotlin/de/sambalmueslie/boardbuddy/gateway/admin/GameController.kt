package de.sambalmueslie.boardbuddy.gateway.admin

import de.sambalmueslie.boardbuddy.core.game.api.GameChangeRequest
import io.micronaut.data.model.Pageable
import io.micronaut.http.annotation.*
import io.swagger.v3.oas.annotations.tags.Tag

@Controller(value = "/api/admin/game")
@Tag(name = "Admin Game API")
class GameController(private val gateway: GameGateway) {

    @Get("{id}")
    fun get(id: Long) = gateway.get(id)

    @Get
    fun getAll(pageable: Pageable) = gateway.getAll(pageable)

    @Post
    fun create(@Body request: GameChangeRequest) = gateway.create(request)

    @Put("{id}")
    fun update(id: Long, @Body request: GameChangeRequest) = gateway.update(id, request)

    @Delete("{id}")
    fun delete(id: Long) = gateway.delete(id)

    @Post("{id}/rule-set/{ruleSetId}")
    fun assignRuleSet(id: Long, ruleSetId: Long) = gateway.assignRuleSet(id, ruleSetId)

    @Delete("{id}/rule-set/{ruleSetId}")
    fun revokeRuleSet(id: Long, ruleSetId: Long) = gateway.revokeRuleSet(id, ruleSetId)
}
