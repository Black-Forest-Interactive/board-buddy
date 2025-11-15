package de.sambalmueslie.boardbuddy.engine.db


import de.sambalmueslie.boardbuddy.common.EntityData
import de.sambalmueslie.boardbuddy.engine.api.GameEntity
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity(name = "GameEntity")
@Table(name = "game_entity")
data class GameEntityData(
    @Id @GeneratedValue var id: Long,
    var created: LocalDateTime,
) : EntityData {
    fun convert(): GameEntity {
        return id
    }
}