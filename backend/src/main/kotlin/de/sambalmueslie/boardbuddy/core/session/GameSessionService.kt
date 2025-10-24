package de.sambalmueslie.boardbuddy.core.session

import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class GameSessionService {
    companion object {
        private val logger = LoggerFactory.getLogger(GameSessionService::class.java)
    }
}