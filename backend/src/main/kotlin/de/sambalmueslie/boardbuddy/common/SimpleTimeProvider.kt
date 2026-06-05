package de.sambalmueslie.boardbuddy.common

import jakarta.inject.Singleton
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Singleton
class SimpleTimeProvider : TimeProvider {
    override fun currentTime(): LocalDateTime {
        return LocalDateTime.now().truncatedTo(ChronoUnit.MICROS)
    }
}