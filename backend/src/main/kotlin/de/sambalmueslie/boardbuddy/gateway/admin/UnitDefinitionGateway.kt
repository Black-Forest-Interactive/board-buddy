package de.sambalmueslie.boardbuddy.gateway.admin

import de.sambalmueslie.boardbuddy.core.unit.UnitDefinitionService
import de.sambalmueslie.boardbuddy.core.unit.api.UnitDefinitionChangeRequest
import io.micronaut.data.model.Pageable
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class UnitDefinitionGateway(
    private val service: UnitDefinitionService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(UnitDefinitionGateway::class.java)
    }

    fun get(id: Long) = service.get(id)
    fun getAll(pageable: Pageable) = service.getAll(pageable)
    fun create(request: UnitDefinitionChangeRequest) = service.create(request)
    fun update(id: Long, request: UnitDefinitionChangeRequest) = service.update(id, request)
    fun delete(id: Long) = service.delete(id)
}