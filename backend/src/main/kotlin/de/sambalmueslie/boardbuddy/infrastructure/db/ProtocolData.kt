package de.sambalmueslie.boardbuddy.infrastructure.db

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity(name = "Protocol")
@Table(name = "protocol")
data class ProtocolData(
    @Id @GeneratedValue var id: Long,
    var app: String,
    var resource: String,
    var timestamp: LocalDateTime,
)
