package de.sambalmueslie.boardbuddy.engine.storage


import de.sambalmueslie.boardbuddy.common.EntityData
import de.sambalmueslie.boardbuddy.engine.api.GameEntity
import de.sambalmueslie.boardbuddy.engine.api.GameEntityType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity(name = "GameEntity")
@Table(name = "game_entity")
data class GameEntityData(
    @Id @GeneratedValue var id: Long,
    @Enumerated(EnumType.STRING) var type: GameEntityType,
    var created: LocalDateTime,
) : EntityData {
    fun convert(): GameEntity {
        return id
    }
}