@file:Suppress("JpaAttributeTypeInspection")

package de.sambalmueslie.boardbuddy.engine.storage

import de.sambalmueslie.boardbuddy.engine.api.UnitProgress
import de.sambalmueslie.boardbuddy.engine.api.UnitProgressEntry
import de.sambalmueslie.boardbuddy.engine.api.UnitType
import io.micronaut.data.annotation.MappedProperty
import io.micronaut.data.model.DataType
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

data class UnitProgressEntryData(
    val level: Int,
    val minDamagePoints: Int,
    val maxDamagePoints: Int,
    val minHealthPoints: Int,
    val maxHealthPoints: Int,
)

@Entity(name = "ComponentUnitProgress")
@Table(name = "component_unit_progress")
data class ComponentUnitProgressData(
    @Id var entityId: Long,

    @field:MappedProperty(type = DataType.JSON)
    var entries: Map<String, UnitProgressEntryData> = emptyMap(),

    var created: LocalDateTime,
    var updated: LocalDateTime? = null
) : GameComponentData<UnitProgress, ComponentUnitProgressData> {
    override fun convert() = UnitProgress(entries.mapKeys { UnitType.valueOf(it.key) }.mapValues { (_, e) -> UnitProgressEntry(e.level, e.minDamagePoints, e.maxDamagePoints, e.minHealthPoints, e.maxHealthPoints) })
    override fun update(value: ComponentUnitProgressData): ComponentUnitProgressData {
        entries = value.entries
        updated = value.created
        return this
    }
}
