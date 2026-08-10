package de.sambalmueslie.boardbuddy.workflow

import de.sambalmueslie.boardbuddy.common.TimeProvider
import de.sambalmueslie.boardbuddy.core.session.GameSessionService
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import kotlin.system.measureTimeMillis

@Singleton
class WorkflowCleanupService(
    private val sessionService: GameSessionService,
    private val workflowService: WorkflowService,
    private val timeProvider: TimeProvider
) {

    companion object {
        private val logger = LoggerFactory.getLogger(WorkflowCleanupService::class.java)
    }

    @Scheduled(cron = "0 0 3 * * *")
    fun cleanup() {
        logger.info("Starting session cleanup")
        val duration = measureTimeMillis {
            val cutoff = timeProvider.currentTime().minusDays(30)
            val sessions = sessionService.findInactiveSince(cutoff)
            sessions.forEach { session ->
                runCatching { workflowService.delete(session) }
                    .onFailure { logger.error("Failed to delete session {}", session.id, it) }
            }
        }
        logger.info("Session cleanup completed within {} ms", duration)
    }
}
