package de.sambalmueslie.boardbuddy.engine.db

import de.sambalmueslie.boardbuddy.engine.api.Level
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity(name = "ComponentLevel")
@Table(name = "component_level")
data class ComponentLevelData(
    @Id var entityId: Long,

    var value: Int,

    var created: LocalDateTime,
    var updated: LocalDateTime? = null
) : GameComponentData<Level, ComponentLevelData> {
    override fun convert() = Level(value)
    override fun update(value: ComponentLevelData): ComponentLevelData {
        this.value = value.value
        this.updated = value.created
        return this
    }
}
