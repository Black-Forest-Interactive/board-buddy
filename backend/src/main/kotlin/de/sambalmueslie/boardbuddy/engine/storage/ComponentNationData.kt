package de.sambalmueslie.boardbuddy.engine.storage

import de.sambalmueslie.boardbuddy.engine.api.Nation
import de.sambalmueslie.boardbuddy.engine.api.NationType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity(name = "ComponentNation  ")
@Table(name = "component_nation")
data class ComponentNationData(
    @Id var entityId: Long,

    @Enumerated(EnumType.STRING) var type: NationType,

    var created: LocalDateTime,
    var updated: LocalDateTime? = null
) : GameComponentData<Nation, ComponentNationData> {
    override fun convert() = Nation(type)
    override fun update(value: ComponentNationData): ComponentNationData {
        type = value.type
        updated = value.created
        return this
    }
}
