package de.sambalmueslie.boardbuddy.engine.storage

import de.sambalmueslie.boardbuddy.engine.api.Technologies
import io.micronaut.data.annotation.MappedProperty
import io.micronaut.data.model.DataType
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity(name = "ComponentTechnology")
@Table(name = "component_technology")
data class ComponentTechnologyData(
    @Id var entityId: Long,

    @field:MappedProperty(type = DataType.JSON)
    var technologies: List<String> = emptyList(),

    var created: LocalDateTime,
    var updated: LocalDateTime? = null
) : GameComponentData<Technologies, ComponentTechnologyData> {

    override fun convert() = Technologies(technologies.map { it.toLong() }.toSet())

    override fun update(value: ComponentTechnologyData): ComponentTechnologyData {
        technologies = value.technologies
        updated = value.created
        return this
    }
}
