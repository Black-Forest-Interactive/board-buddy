package de.sambalmueslie.boardbuddy.infrastructure

import de.sambalmueslie.boardbuddy.common.TimeProvider
import de.sambalmueslie.boardbuddy.infrastructure.db.ProtocolData
import de.sambalmueslie.boardbuddy.infrastructure.db.ProtocolEntryRepository
import de.sambalmueslie.boardbuddy.infrastructure.db.ProtocolRepository
import jakarta.inject.Singleton

@Singleton
class ProtocolService(
    private val repository: ProtocolRepository,
    private val entryRepository: ProtocolEntryRepository,
    private val timeProvider: TimeProvider
) {

    fun getProtocol(app: String, resource: String): Protocol {
        val data = repository.findByAppAndResource(app, resource) ?: repository.save(ProtocolData(0, app, resource, timeProvider.currentTime()))
        return Protocol(data, entryRepository, timeProvider)
    }
}