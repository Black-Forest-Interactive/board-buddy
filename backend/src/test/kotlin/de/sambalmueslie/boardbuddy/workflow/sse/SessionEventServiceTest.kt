package de.sambalmueslie.boardbuddy.workflow.sse

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.concurrent.CopyOnWriteArrayList

class SessionEventServiceTest {

    @Test
    fun `events reach a subscriber that reconnects after everyone left`() {
        val service = SessionEventService()

        val first = service.subscribe("s1").subscribe()
        first.dispose()

        val received = CopyOnWriteArrayList<SessionEventType>()
        val second = service.subscribe("s1").subscribe { received.add(it.type) }
        service.emit("s1", SessionEventType.BATTLE_STARTED)
        second.dispose()

        assertEquals(listOf(SessionEventType.BATTLE_STARTED), received)
    }

    @Test
    fun `emit without subscribers does not create a channel`() {
        val service = SessionEventService()
        service.emit("unknown", SessionEventType.BATTLE_STARTED)

        val received = CopyOnWriteArrayList<SessionEventType>()
        val sub = service.subscribe("unknown").subscribe { received.add(it.type) }
        sub.dispose()
        assertEquals(emptyList<SessionEventType>(), received)
    }
}
