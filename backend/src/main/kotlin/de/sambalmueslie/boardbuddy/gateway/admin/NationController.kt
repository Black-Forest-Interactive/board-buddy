package de.sambalmueslie.boardbuddy.gateway.admin

import de.sambalmueslie.boardbuddy.core.nation.api.NationChangeRequest
import de.sambalmueslie.boardbuddy.core.nation.api.NationEffectInitialGovernmentRequest
import de.sambalmueslie.boardbuddy.engine.api.GovernmentType
import io.micronaut.data.model.Pageable
import io.micronaut.http.annotation.*
import io.swagger.v3.oas.annotations.tags.Tag

@Controller(value = "/api/portal/nation")
@Tag(name = "Admin Nation API")
class NationController(private val gateway: NationGateway) {

    @Get("{id}")
    fun get(id: Long) = gateway.get(id)

    @Get
    fun getAll(pageable: Pageable) = gateway.getAll(pageable)

    @Post
    fun create(@Body request: NationChangeRequest) = gateway.create(request)

    @Put("{id}")
    fun update(id: Long, @Body request: NationChangeRequest) = gateway.update(id, request)

    @Delete("{id}")
    fun delete(id: Long) = gateway.delete(id)

    @Post("{id}/effect/initial-government")
    fun assignInitialGovernment(id: Long, @Body request: NationEffectInitialGovernmentRequest) =
        gateway.assignInitialGovernment(id, request)

    @Delete("{id}/effect/initial-government/{type}")
    fun revokeInitialGovernment(id: Long, type: GovernmentType) =
        gateway.revokeInitialGovernment(id, type)
}
