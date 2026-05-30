package de.sambalmueslie.boardbuddy.workflow.sse

data class SessionEvent(
    val sessionKey: String,
    val type: SessionEventType,
)
