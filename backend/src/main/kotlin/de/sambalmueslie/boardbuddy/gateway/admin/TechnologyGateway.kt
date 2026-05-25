package de.sambalmueslie.boardbuddy.gateway.admin

import de.sambalmueslie.boardbuddy.core.technology.TechnologyService
import de.sambalmueslie.boardbuddy.core.technology.api.TechnologyChangeRequest
import de.sambalmueslie.boardbuddy.core.technology.api.TechnologyEffect
import de.sambalmueslie.boardbuddy.core.technology.api.TechnologyEffectUnitUnlockRequest
import de.sambalmueslie.boardbuddy.engine.api.UnitType
import io.micronaut.data.model.Pageable
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class TechnologyGateway(
    private val service: TechnologyService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(TechnologyGateway::class.java)
    }

    fun get(id: Long) = service.get(id)
    fun getAll(pageable: Pageable) = service.getAll(pageable)
    fun create(request: TechnologyChangeRequest) = service.create(request)
    fun update(id: Long, request: TechnologyChangeRequest) = service.update(id, request)
    fun delete(id: Long) = service.delete(id)

    fun assignUnitUnlock(id: Long, request: TechnologyEffectUnitUnlockRequest) =
        service.assignTechnology(id, TechnologyEffect.UnitUnlock(request.unitType, request.unitLevel))

    fun revokeUnitUnlock(id: Long, unitType: UnitType, unitLevel: Int) =
        service.revokeTechnology(id, TechnologyEffect.UnitUnlock(unitType, unitLevel))
}
