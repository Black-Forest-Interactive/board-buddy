package de.sambalmueslie.boardbuddy.core.ruleset

import de.sambalmueslie.boardbuddy.core.common.BaseEntityService
import de.sambalmueslie.boardbuddy.core.common.TimeProvider
import de.sambalmueslie.boardbuddy.core.event.EventService
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSet
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSetChangeRequest
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSetNameValidationFailed
import de.sambalmueslie.boardbuddy.core.ruleset.db.RuleSetData
import de.sambalmueslie.boardbuddy.core.ruleset.db.RuleSetRepository
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class RuleSetService(
    repository: RuleSetRepository,
    eventService: EventService,
    private val timeProvider: TimeProvider
) : BaseEntityService<RuleSet, RuleSetChangeRequest, RuleSetData>(repository, eventService, RuleSet::class) {


    companion object {
        private val logger = LoggerFactory.getLogger(RuleSetService::class.java)
    }

    override fun convert(data: RuleSetData): RuleSet {
        return data.convert()
    }

    override fun createData(request: RuleSetChangeRequest): RuleSetData {
        return RuleSetData(0, request.name, timeProvider.currentTime())
    }

    override fun updateData(existing: RuleSetData, request: RuleSetChangeRequest): RuleSetData {
        return existing.update(request, timeProvider.currentTime())
    }

    override fun validate(request: RuleSetChangeRequest) {
        if (request.name.isBlank()) throw RuleSetNameValidationFailed(request.name)
    }

}