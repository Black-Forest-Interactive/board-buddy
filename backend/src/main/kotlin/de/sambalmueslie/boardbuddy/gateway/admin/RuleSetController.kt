package de.sambalmueslie.boardbuddy.gateway.admin

import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSetChangeRequest
import io.micronaut.data.model.Pageable
import io.micronaut.http.annotation.*
import io.swagger.v3.oas.annotations.tags.Tag

@Controller(value = "/api/portal/rule-set")
@Tag(name = "Admin Rule Set API")
class RuleSetController(private val gateway: RuleSetGateway) {

    @Get("{id}")
    fun get(id: Long) = gateway.get(id)

    @Get
    fun getAll(pageable: Pageable) = gateway.getAll(pageable)

    @Post
    fun create(@Body request: RuleSetChangeRequest) = gateway.create(request)

    @Put("{id}")
    fun update(id: Long, @Body request: RuleSetChangeRequest) = gateway.update(id, request)

    @Delete("{id}")
    fun delete(id: Long) = gateway.delete(id)

    @Post("{id}/unit-definition/{unitDefinitionId}")
    fun assignUnitDefinition(id: Long, unitDefinitionId: Long) =
        gateway.assignUnitDefinition(id, unitDefinitionId)

    @Delete("{id}/unit-definition/{unitDefinitionId}")
    fun revokeUnitDefinition(id: Long, unitDefinitionId: Long) =
        gateway.revokeUnitDefinition(id, unitDefinitionId)

    @Post("{id}/technology/{technologyId}")
    fun assignTechnology(id: Long, technologyId: Long) =
        gateway.assignTechnology(id, technologyId)

    @Delete("{id}/technology/{technologyId}")
    fun revokeTechnology(id: Long, technologyId: Long) =
        gateway.revokeTechnology(id, technologyId)

    @Post("{id}/nation/{nationId}")
    fun assignNation(id: Long, nationId: Long) = gateway.assignNation(id, nationId)

    @Delete("{id}/nation/{nationId}")
    fun revokeNation(id: Long, nationId: Long) = gateway.revokeNation(id, nationId)
}
