package de.sambalmueslie.boardbuddy.engine.storage

import de.sambalmueslie.boardbuddy.engine.api.NationReference
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity(name = "ComponentNation")
@Table(name = "component_nation")
data class ComponentNationData(
    @Id var entityId: Long,

    var nationId: Long,

    var created: LocalDateTime,
    var updated: LocalDateTime? = null
) : GameComponentData<NationReference, ComponentNationData> {
    override fun convert() = NationReference(nationId)
    override fun update(value: ComponentNationData): ComponentNationData {
        nationId = value.nationId
        updated = value.created
        return this
    }
}
