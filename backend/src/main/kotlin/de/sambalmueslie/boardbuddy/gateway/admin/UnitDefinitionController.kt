package de.sambalmueslie.boardbuddy.gateway.admin

import de.sambalmueslie.boardbuddy.core.unit.api.UnitDefinitionChangeRequest
import io.micronaut.data.model.Pageable
import io.micronaut.http.annotation.*
import io.swagger.v3.oas.annotations.tags.Tag

@Controller(value = "/api/portal/unit-type")
@Tag(name = "Admin Unit Type API")
class UnitDefinitionController(private val gateway: UnitDefinitionGateway) {

    @Get("{id}")
    fun get(id: Long) = gateway.get(id)

    @Get
    fun getAll(pageable: Pageable) = gateway.getAll(pageable)

    @Post
    fun create(@Body request: UnitDefinitionChangeRequest) = gateway.create(request)

    @Put("{id}")
    fun update(id: Long, @Body request: UnitDefinitionChangeRequest) = gateway.update(id, request)

    @Delete("{id}")
    fun delete(id: Long) = gateway.delete(id)
}