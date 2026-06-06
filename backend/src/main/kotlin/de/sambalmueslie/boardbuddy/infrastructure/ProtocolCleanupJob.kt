package de.sambalmueslie.boardbuddy.infrastructure

import de.sambalmueslie.boardbuddy.common.TimeProvider
import de.sambalmueslie.boardbuddy.infrastructure.db.ProtocolEntryRepository
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import kotlin.system.measureTimeMillis

@Singleton
class ProtocolCleanupJob(
    private val entryRepository: ProtocolEntryRepository,
    private val timeProvider: TimeProvider
) {

    companion object {
        private val logger = LoggerFactory.getLogger(ProtocolCleanupJob::class.java)
    }

    @Scheduled(cron = "0 0 2 * * *")
    fun cleanup() {
        logger.info("Cleaning up protocol entries")
        val duration = measureTimeMillis {
            val cutoff = timeProvider.currentTime().minusDays(30)
            entryRepository.deleteByTimestampBefore(cutoff)
        }
        logger.info("Cleaned up protocol entries within $duration ms")
    }
}
