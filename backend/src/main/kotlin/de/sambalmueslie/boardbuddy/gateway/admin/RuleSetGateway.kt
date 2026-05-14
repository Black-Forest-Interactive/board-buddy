package de.sambalmueslie.boardbuddy.gateway.admin

import de.sambalmueslie.boardbuddy.core.ruleset.RuleSetService
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSetChangeRequest
import io.micronaut.data.model.Pageable
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class RuleSetGateway(
    private val service: RuleSetService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(RuleSetGateway::class.java)
    }

    fun get(id: Long) = service.get(id)
    fun getAll(pageable: Pageable) = service.getAll(pageable)
    fun create(request: RuleSetChangeRequest) = service.create(request)
    fun update(id: Long, request: RuleSetChangeRequest) = service.update(id, request)
    fun delete(id: Long) = service.delete(id)
}