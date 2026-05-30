package de.sambalmueslie.boardbuddy.core.nation.db

import de.sambalmueslie.boardbuddy.common.EntityData
import de.sambalmueslie.boardbuddy.core.nation.api.Nation
import de.sambalmueslie.boardbuddy.core.nation.api.NationChangeRequest
import de.sambalmueslie.boardbuddy.core.nation.api.NationEffect
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity(name = "Nation")
@Table(name = "nation")
data class NationData(
    @Id @GeneratedValue var id: Long,
    var name: String,
    var description: String,
    var imageUrl: String,
    var created: LocalDateTime,
    var updated: LocalDateTime? = null
) : EntityData {
    fun convert(effects: List<NationEffect>) = Nation(id, name, description, imageUrl, effects)
    fun update(request: NationChangeRequest, currentTime: LocalDateTime): NationData {
        name = request.name
        description = request.description
        imageUrl = request.imageUrl
        updated = currentTime
        return this
    }
}


