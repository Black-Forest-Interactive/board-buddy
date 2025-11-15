package de.sambalmueslie.boardbuddy.engine.db

import de.sambalmueslie.boardbuddy.engine.api.Damage
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity(name = "ComponentDamage")
@Table(name = "component_damage")
data class ComponentDamageData(
    @Id var entityId: Long,

    var amount: Int,

    var created: LocalDateTime,
    var updated: LocalDateTime? = null
) : GameComponentData<Damage, ComponentDamageData> {
    override fun convert() = Damage(amount)
    override fun update(value: ComponentDamageData): ComponentDamageData {
        amount = value.amount
        updated = value.created
        return this
    }
}
