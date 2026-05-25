package de.sambalmueslie.boardbuddy.core.nation.db

import de.sambalmueslie.boardbuddy.common.EntityData
import de.sambalmueslie.boardbuddy.core.nation.api.NationEffect
import de.sambalmueslie.boardbuddy.engine.api.GovernmentType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity(name = "NationEffectInitialGovernment")
@Table(name = "nation_effect_initial_government")
data class NationEffectInitialGovernmentData(
    @Id @GeneratedValue var id: Long,
    var nationId: Long,
    @Enumerated(EnumType.STRING) var type: GovernmentType,
    var created: LocalDateTime,
    var updated: LocalDateTime? = null
) : EntityData {
    fun convert() = NationEffect.InitialGovernment(type)
}
