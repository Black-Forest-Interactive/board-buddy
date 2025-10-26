package de.sambalmueslie.boardbuddy.core.ruleset.db

import de.sambalmueslie.boardbuddy.core.common.EntityData
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSet
import de.sambalmueslie.boardbuddy.core.ruleset.api.RuleSetChangeRequest
import de.sambalmueslie.boardbuddy.core.unit.api.UnitType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime


@Entity(name = "RuleSet")
@Table(name = "rule_set")
data class RuleSetData(
    @Id @GeneratedValue var id: Long,
    var name: String,
    var created: LocalDateTime,
    var updated: LocalDateTime? = null
) : EntityData {
    fun convert(unitTypes: List<UnitType>) = RuleSet(id, name, unitTypes)
    fun update(request: RuleSetChangeRequest, currentTime: LocalDateTime): RuleSetData {
        name = request.name
        updated = currentTime
        return this
    }
}
