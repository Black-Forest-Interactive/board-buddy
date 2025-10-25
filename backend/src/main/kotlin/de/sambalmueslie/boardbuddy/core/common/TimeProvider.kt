package de.sambalmueslie.boardbuddy.core.common

import java.time.LocalDateTime

interface TimeProvider {
    fun currentTime(): LocalDateTime
}