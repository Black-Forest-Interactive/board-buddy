package de.sambalmueslie.boardbuddy.engine.storage

import de.sambalmueslie.boardbuddy.engine.api.CounterType
import de.sambalmueslie.boardbuddy.engine.api.UnitType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity(name = "ComponentCounterType")
@Table(name = "component_counter_type")
data class ComponentCounterTypeData(
    @Id var entityId: Long,

    @Enumerated(EnumType.STRING) var kind: UnitType,

    var created: LocalDateTime,
    var updated: LocalDateTime? = null
) : GameComponentData<CounterType, ComponentCounterTypeData> {
    override fun convert() = CounterType(kind)

    override fun update(value: ComponentCounterTypeData): ComponentCounterTypeData {
        kind = value.kind
        updated = value.created
        return this
    }
}
