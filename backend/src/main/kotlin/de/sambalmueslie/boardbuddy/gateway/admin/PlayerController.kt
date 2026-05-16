package de.sambalmueslie.boardbuddy.gateway.admin

import de.sambalmueslie.boardbuddy.core.player.api.PlayerChangeRequest
import io.micronaut.data.model.Pageable
import io.micronaut.http.annotation.*
import io.swagger.v3.oas.annotations.tags.Tag

@Controller(value = "/api/admin/player")
@Tag(name = "Admin Player API")
class PlayerController(private val gateway: PlayerGateway) {

    @Get("{id}")
    fun get(id: Long) = gateway.get(id)

    @Get
    fun getAll(pageable: Pageable) = gateway.getAll(pageable)

    @Post
    fun create(@Body request: PlayerChangeRequest) = gateway.create(request)

    @Put("{id}")
    fun update(id: Long, @Body request: PlayerChangeRequest) = gateway.update(id, request)

    @Delete("{id}")
    fun delete(id: Long) = gateway.delete(id)
}