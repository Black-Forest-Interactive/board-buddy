package de.sambalmueslie.boardbuddy.core.technology.db

import de.sambalmueslie.boardbuddy.common.EntityData
import de.sambalmueslie.boardbuddy.core.technology.api.TechnologyEffect
import de.sambalmueslie.boardbuddy.engine.api.UnitType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity(name = "TechnologyEffectUnitUnlock")
@Table(name = "technology_effect_unit_unlock")
data class TechnologyEffectUnitUnlockData(
    @Id @GeneratedValue var id: Long,
    var technologyId: Long,
    @Enumerated(EnumType.STRING) var unitType: UnitType,
    var unitLevel: Int,
    var created: LocalDateTime,
    var updated: LocalDateTime? = null
) : EntityData {
    fun convert() = TechnologyEffect.UnitUnlock(unitType, unitLevel)
}
