package de.sambalmueslie.boardbuddy.engine.db

import de.sambalmueslie.boardbuddy.engine.api.Health
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity(name = "ComponentHealth")
@Table(name = "component_health")
data class ComponentHealthData(
    @Id var entityId: Long,

    var amount: Int,

    var created: LocalDateTime,
    var updated: LocalDateTime? = null
) : GameComponentData<Health, ComponentHealthData> {
    override fun convert() = Health(amount)

    override fun update(value: ComponentHealthData): ComponentHealthData {
        amount = value.amount
        updated = value.created
        return this
    }
}
