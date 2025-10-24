package de.sambalmueslie.boardbuddy.core.common

import jakarta.inject.Singleton
import java.time.LocalDateTime

@Singleton
class SimpleTimeProvider : TimeProvider {
    override fun currentTime(): LocalDateTime {
        return LocalDateTime.now()
    }
}