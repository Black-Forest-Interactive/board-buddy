package de.sambalmueslie.boardbuddy.core.event.api

data class ChangeEvent<T>(
    val content: T,
    val type: ChangeType
)
