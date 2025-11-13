package de.sambalmueslie.boardbuddy.common

import java.time.LocalDateTime

interface TimeProvider {
    fun currentTime(): LocalDateTime
}