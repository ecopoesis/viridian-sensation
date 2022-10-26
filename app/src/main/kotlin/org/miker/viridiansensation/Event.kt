package org.miker.viridiansensation

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

data class Event(
    val url: String,
    val visitorId: String, // maybe UUID
    val timestamp: Long
) {
    val dateTime: LocalDateTime by lazy {
        LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC)
    }
}
