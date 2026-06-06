package de.sambalmueslie.boardbuddy.infrastructure.db

import io.micronaut.data.annotation.MappedProperty
import io.micronaut.data.model.DataType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity(name = "ProtocolEntry")
@Table(name = "protocol_entry")
data class ProtocolEntryData(
    @Id @GeneratedValue var id: Long,
    var protocolId: Long,
    var message: String,
    @field:MappedProperty(type = DataType.JSON)
    var payload: Any?,
    var timestamp: LocalDateTime,
)
