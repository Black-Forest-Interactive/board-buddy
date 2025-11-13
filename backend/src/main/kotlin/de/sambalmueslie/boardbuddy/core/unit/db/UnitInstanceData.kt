package de.sambalmueslie.boardbuddy.core.unit.db

import de.sambalmueslie.boardbuddy.common.EntityData
import de.sambalmueslie.boardbuddy.core.unit.api.UnitDefinition
import de.sambalmueslie.boardbuddy.core.unit.api.UnitInstance
import de.sambalmueslie.boardbuddy.core.unit.api.UnitInstanceChangeRequest
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity(name = "UnitInstance")
@Table(name = "unit_instance")
data class UnitInstanceData(
    @Id @GeneratedValue var id: Long,
    var unitTypeId: Long,

    var damage: Int,
    var health: Int,
    var level: Int,

    var created: LocalDateTime,
    var updated: LocalDateTime? = null

) : EntityData {
    fun convert(type: UnitDefinition) = UnitInstance(id, type, damage, health, level)

    fun update(request: UnitInstanceChangeRequest, currentTime: LocalDateTime): UnitInstanceData {
        health = request.health
        damage = request.damage
        level = request.level
        updated = currentTime
        return this
    }
}
