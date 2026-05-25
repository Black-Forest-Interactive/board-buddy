package de.sambalmueslie.boardbuddy.gateway.admin

import de.sambalmueslie.boardbuddy.core.ruleset.RuleSetService
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSetChangeRequest
import de.sambalmueslie.boardbuddy.core.technology.TechnologyService
import de.sambalmueslie.boardbuddy.core.unit.UnitDefinitionService
import io.micronaut.data.model.Pageable
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class RuleSetGateway(
    private val service: RuleSetService,
    private val unitDefinitionService: UnitDefinitionService,
    private val technologyService: TechnologyService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(RuleSetGateway::class.java)
    }

    fun get(id: Long) = service.get(id)
    fun getAll(pageable: Pageable) = service.getAll(pageable)
    fun create(request: RuleSetChangeRequest) = service.create(request)
    fun update(id: Long, request: RuleSetChangeRequest) = service.update(id, request)
    fun delete(id: Long) = service.delete(id)

    fun assignUnitDefinition(ruleSetId: Long, unitDefinitionId: Long) =
        unitDefinitionService.get(unitDefinitionId)
            ?.let { service.assignUnitDefinition(ruleSetId, it) }

    fun revokeUnitDefinition(ruleSetId: Long, unitDefinitionId: Long) =
        unitDefinitionService.get(unitDefinitionId)
            ?.let { service.revokeUnitDefinition(ruleSetId, it) }

    fun assignTechnology(ruleSetId: Long, technologyId: Long) =
        technologyService.get(technologyId)
            ?.let { service.assignTechnology(ruleSetId, it) }

    fun revokeTechnology(ruleSetId: Long, technologyId: Long) =
        technologyService.get(technologyId)
            ?.let { service.revokeTechnology(ruleSetId, it) }
}
