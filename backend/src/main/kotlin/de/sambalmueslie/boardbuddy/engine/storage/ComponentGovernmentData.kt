package de.sambalmueslie.boardbuddy.engine.storage

import de.sambalmueslie.boardbuddy.engine.api.Government
import de.sambalmueslie.boardbuddy.engine.api.GovernmentType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity(name = "ComponentGovernment  ")
@Table(name = "component_government")
data class ComponentGovernmentData(
    @Id var entityId: Long,

    @Enumerated(EnumType.STRING) var type: GovernmentType,

    var created: LocalDateTime,
    var updated: LocalDateTime? = null
) : GameComponentData<Government, ComponentGovernmentData> {
    override fun convert() = Government(type)
    override fun update(value: ComponentGovernmentData): ComponentGovernmentData {
        type = value.type
        updated = value.created
        return this
    }
}
