package de.sambalmueslie.boardbuddy.infrastructure

import de.sambalmueslie.boardbuddy.common.TimeProvider
import de.sambalmueslie.boardbuddy.infrastructure.db.ProtocolData
import de.sambalmueslie.boardbuddy.infrastructure.db.ProtocolEntryData
import de.sambalmueslie.boardbuddy.infrastructure.db.ProtocolEntryRepository

class Protocol(
    private val data: ProtocolData,
    private val repository: ProtocolEntryRepository,
    private val timeProvider: TimeProvider
) {


    fun log(message: String) {
        addEntry(message)
    }

    fun log(message: String, payload: Any) {
        addEntry(message, payload)
    }

    fun <T> log(message: String, action: () -> T): T {
        addEntry(message)
        return action.invoke()
    }

    fun <T> log(message: String, payload: Any, action: () -> T): T {
        addEntry(message, payload)
        return action.invoke()
    }

    private fun addEntry(message: String, payload: Any? = null) {
        val entry = ProtocolEntryData(0, data.id, message, payload, timeProvider.currentTime())
        repository.save(entry)
    }
}