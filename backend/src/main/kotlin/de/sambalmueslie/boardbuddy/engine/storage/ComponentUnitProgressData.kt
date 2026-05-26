@file:Suppress("JpaAttributeTypeInspection")

package de.sambalmueslie.boardbuddy.engine.storage

import de.sambalmueslie.boardbuddy.engine.api.UnitProgress
import de.sambalmueslie.boardbuddy.engine.api.UnitType
import io.micronaut.data.annotation.MappedProperty
import io.micronaut.data.model.DataType
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity(name = "ComponentUnitProgress")
@Table(name = "component_unit_progress")
data class ComponentUnitProgressData(
    @Id var entityId: Long,

    @field:MappedProperty(type = DataType.JSON)
    var levels: Map<String, Int> = emptyMap(),

    var created: LocalDateTime,
    var updated: LocalDateTime? = null
) : GameComponentData<UnitProgress, ComponentUnitProgressData> {
    override fun convert() = UnitProgress(levels.mapKeys { UnitType.valueOf(it.key) })
    override fun update(value: ComponentUnitProgressData): ComponentUnitProgressData {
        levels = value.levels
        updated = value.created
        return this
    }
}
