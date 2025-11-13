package de.sambalmueslie.boardbuddy.core.unit.db

import de.sambalmueslie.boardbuddy.common.EntityData
import de.sambalmueslie.boardbuddy.core.unit.api.PointsRange
import de.sambalmueslie.boardbuddy.core.unit.api.UnitDefinition
import de.sambalmueslie.boardbuddy.core.unit.api.UnitDefinitionChangeRequest
import de.sambalmueslie.boardbuddy.engine.api.UnitType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity(name = "UnitDefinition")
@Table(name = "unit_definition")
data class UnitDefinitionData(
    @Id @GeneratedValue var id: Long,
    var name: String,
    @Enumerated(EnumType.STRING) var unitType: UnitType,
    @Enumerated(EnumType.STRING) var counterClass: UnitType?,
    var minDamagePoints: Int,
    var maxDamagePoints: Int,
    var minHealthPoints: Int,
    var maxHealthPoints: Int,
    var maxLevel: Int,

    var created: LocalDateTime,
    var updated: LocalDateTime? = null
) : EntityData {
    fun convert() = UnitDefinition(id, name, unitType, counterClass, PointsRange(minDamagePoints, maxDamagePoints), PointsRange(minHealthPoints, maxHealthPoints), maxLevel)
    fun update(request: UnitDefinitionChangeRequest, currentTime: LocalDateTime): UnitDefinitionData {
        name = request.name
        unitType = request.unitType
        counterClass = request.counterClass
        minDamagePoints = request.damagePoints.min
        maxDamagePoints = request.damagePoints.max
        minHealthPoints = request.damagePoints.min
        maxHealthPoints = request.damagePoints.max
        maxLevel = request.maxLevel
        updated = currentTime
        return this
    }
}


