package de.sambalmueslie.boardbuddy.core.unit.db

import de.sambalmueslie.boardbuddy.core.common.EntityData
import de.sambalmueslie.boardbuddy.core.unit.api.PointsRange
import de.sambalmueslie.boardbuddy.core.unit.api.UnitClass
import de.sambalmueslie.boardbuddy.core.unit.api.UnitType
import de.sambalmueslie.boardbuddy.core.unit.api.UnitTypeChangeRequest
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity(name = "UnitType")
@Table(name = "unit_type")
data class UnitTypeData(
    @Id @GeneratedValue var id: Long,
    var name: String,
    @Enumerated(EnumType.STRING) var unitClass: UnitClass,
    @Enumerated(EnumType.STRING) var counterClass: UnitClass?,
    var minDamagePoints: Int,
    var maxDamagePoints: Int,
    var minHealthPoints: Int,
    var maxHealthPoints: Int,
    var maxLevel: Int,

    var created: LocalDateTime,
    var updated: LocalDateTime? = null
) : EntityData {
    fun convert() = UnitType(id, name, unitClass, counterClass, PointsRange(minDamagePoints, maxDamagePoints), PointsRange(minHealthPoints, maxHealthPoints), maxLevel)
    fun update(request: UnitTypeChangeRequest, currentTime: LocalDateTime): UnitTypeData {
        name = request.name
        unitClass = request.unitClass
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


