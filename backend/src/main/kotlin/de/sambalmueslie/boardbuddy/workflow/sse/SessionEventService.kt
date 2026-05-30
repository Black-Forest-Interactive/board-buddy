package de.sambalmueslie.boardbuddy.workflow.sse

import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.util.concurrent.ConcurrentHashMap

@Singleton
class SessionEventService {
    companion object {
        private val logger = LoggerFactory.getLogger(SessionEventService::class.java)
    }

    private val sinks = ConcurrentHashMap<String, Sinks.Many<SessionEvent>>()

    fun subscribe(sessionKey: String): Flux<SessionEvent> {
        val sink = sinks.computeIfAbsent(sessionKey) {
            Sinks.many().multicast().onBackpressureBuffer()
        }
        return sink.asFlux()
    }

    fun emit(sessionKey: String, type: SessionEventType) {
        val sink = sinks[sessionKey] ?: return
        val result = sink.tryEmitNext(SessionEvent(sessionKey, type))
        if (result.isFailure) logger.warn("Failed to emit {} for session {}: {}", type, sessionKey, result)
    }

    fun cleanup(sessionKey: String) {
        sinks.remove(sessionKey)?.tryEmitComplete()
    }
}
