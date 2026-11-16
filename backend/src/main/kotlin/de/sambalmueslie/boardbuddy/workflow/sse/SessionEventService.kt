package de.sambalmueslie.boardbuddy.workflow.sse

import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * One multicast sink per session. A sink only exists while somebody listens: it is removed again
 * when the last subscriber leaves, so unknown or abandoned session keys do not accumulate memory.
 */
@Singleton
class SessionEventService {
    companion object {
        private val logger = LoggerFactory.getLogger(SessionEventService::class.java)
        private const val MAX_SUBSCRIBERS_PER_SESSION = 50
        private const val BUFFER_SIZE = 64
    }

    private class Channel {
        // autoCancel = false: the sink must survive a temporary drop to zero subscribers until it is removed
        val sink: Sinks.Many<SessionEvent> = Sinks.many().multicast().onBackpressureBuffer(BUFFER_SIZE, false)
        val subscribers = AtomicInteger(0)
    }

    private val channels = ConcurrentHashMap<String, Channel>()

    fun subscribe(sessionKey: String): Flux<SessionEvent> = Flux.defer {
        val channel = channels.compute(sessionKey) { _, existing ->
            val c = existing ?: Channel()
            if (c.subscribers.get() >= MAX_SUBSCRIBERS_PER_SESSION) throw SessionEventLimitReached(sessionKey)
            c.subscribers.incrementAndGet()
            c
        }!!
        channel.sink.asFlux().doFinally { release(sessionKey, channel) }
    }

    private fun release(sessionKey: String, channel: Channel) {
        channels.computeIfPresent(sessionKey) { _, current ->
            if (current !== channel) return@computeIfPresent current
            if (current.subscribers.decrementAndGet() <= 0) null else current
        }
    }

    fun emit(sessionKey: String, type: SessionEventType) {
        val channel = channels[sessionKey] ?: return
        val result = channel.sink.tryEmitNext(SessionEvent(sessionKey, type))
        if (result.isFailure) logger.warn("Failed to emit {} for session {}: {}", type, sessionKey, result)
    }

    fun cleanup(sessionKey: String) {
        channels.remove(sessionKey)?.sink?.tryEmitComplete()
    }
}

class SessionEventLimitReached(sessionKey: String) : RuntimeException("Too many event subscribers for session $sessionKey")
