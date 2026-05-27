package de.sambalmueslie.boardbuddy.gateway.portal

import de.sambalmueslie.boardbuddy.core.game.api.GameChangeRequest
import io.micronaut.data.model.Pageable
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.swagger.v3.oas.annotations.tags.Tag

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller(value = "/api/portal/game")
@Tag(name = "Portal Game API")
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
}