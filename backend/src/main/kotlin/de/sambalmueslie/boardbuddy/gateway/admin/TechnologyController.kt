package de.sambalmueslie.boardbuddy.gateway.admin

import de.sambalmueslie.boardbuddy.core.technology.api.TechnologyChangeRequest
import de.sambalmueslie.boardbuddy.core.technology.api.TechnologyEffectUnitUnlockRequest
import de.sambalmueslie.boardbuddy.engine.api.UnitType
import io.micronaut.data.model.Pageable
import io.micronaut.http.annotation.*
import io.swagger.v3.oas.annotations.tags.Tag

@Controller(value = "/api/portal/technology")
@Tag(name = "Admin Technology API")
class TechnologyController(private val gateway: TechnologyGateway) {

    @Get("{id}")
    fun get(id: Long) = gateway.get(id)

    @Get
    fun getAll(pageable: Pageable) = gateway.getAll(pageable)

    @Post
    fun create(@Body request: TechnologyChangeRequest) = gateway.create(request)

    @Put("{id}")
    fun update(id: Long, @Body request: TechnologyChangeRequest) = gateway.update(id, request)

    @Delete("{id}")
    fun delete(id: Long) = gateway.delete(id)

    @Post("{id}/effect/unit-unlock")
    fun assignUnitUnlock(id: Long, @Body request: TechnologyEffectUnitUnlockRequest) = gateway.assignUnitUnlock(id, request)

    @Delete("{id}/effect/unit-unlock/{unitType}/{unitLevel}")
    fun revokeUnitUnlock(id: Long, unitType: UnitType, unitLevel: Int) = gateway.revokeUnitUnlock(id, unitType, unitLevel)
}
