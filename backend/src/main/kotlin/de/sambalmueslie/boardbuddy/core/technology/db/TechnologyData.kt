package de.sambalmueslie.boardbuddy.core.technology.db

import de.sambalmueslie.boardbuddy.common.EntityData
import de.sambalmueslie.boardbuddy.core.technology.api.Technology
import de.sambalmueslie.boardbuddy.core.technology.api.TechnologyChangeRequest
import de.sambalmueslie.boardbuddy.core.technology.api.TechnologyEffect
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity(name = "Technology")
@Table(name = "technology")
data class TechnologyData(
    @Id @GeneratedValue var id: Long,
    var name: String,
    var description: String,
    var imageUrl: String,
    var tier: Int,
    var created: LocalDateTime,
    var updated: LocalDateTime? = null
) : EntityData {
    fun convert(effects: List<TechnologyEffect>) = Technology(id, name, description, imageUrl, tier, effects)
    fun update(request: TechnologyChangeRequest, currentTime: LocalDateTime): TechnologyData {
        name = request.name
        description = request.description
        imageUrl = request.imageUrl
        tier = request.tier
        updated = currentTime
        return this
    }
}

