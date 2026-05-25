package de.sambalmueslie.boardbuddy.gateway.admin

import de.sambalmueslie.boardbuddy.core.nation.NationService
import de.sambalmueslie.boardbuddy.core.nation.api.NationChangeRequest
import de.sambalmueslie.boardbuddy.core.nation.api.NationEffect
import de.sambalmueslie.boardbuddy.core.nation.api.NationEffectInitialGovernmentRequest
import de.sambalmueslie.boardbuddy.engine.api.GovernmentType
import io.micronaut.data.model.Pageable
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class NationGateway(private val service: NationService) {
    companion object {
        private val logger = LoggerFactory.getLogger(NationGateway::class.java)
    }

    fun get(id: Long) = service.get(id)
    fun getAll(pageable: Pageable) = service.getAll(pageable)
    fun create(request: NationChangeRequest) = service.create(request)
    fun update(id: Long, request: NationChangeRequest) = service.update(id, request)
    fun delete(id: Long) = service.delete(id)

    fun assignInitialGovernment(id: Long, request: NationEffectInitialGovernmentRequest) =
        service.assignNationEffect(id, NationEffect.InitialGovernment(request.type))

    fun revokeInitialGovernment(id: Long, type: GovernmentType) =
        service.revokeNationEffect(id, NationEffect.InitialGovernment(type))
}
