package de.sambalmueslie.boardbuddy.engine.db

import de.sambalmueslie.boardbuddy.engine.api.Type
import de.sambalmueslie.boardbuddy.engine.api.UnitType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity(name = "ComponentType")
@Table(name = "component_type")
data class ComponentTypeData(
    @Id var entityId: Long,

    @Enumerated(EnumType.STRING) var kind: UnitType,

    var created: LocalDateTime,
    var updated: LocalDateTime? = null
) : GameComponentData<Type, ComponentTypeData> {
    override fun convert() = Type(kind)
    override fun update(value: ComponentTypeData): ComponentTypeData {
        kind = value.kind
        updated = value.created
        return this
    }
}
