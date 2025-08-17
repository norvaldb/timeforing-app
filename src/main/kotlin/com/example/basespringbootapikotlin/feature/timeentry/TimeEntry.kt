package com.example.basespringbootapikotlin.feature.timeentry

import java.time.LocalDate
import java.time.LocalDateTime

data class TimeEntry(
    val timeEntryId: Long? = null,
    val prosjektId: Long,
    val userSub: String,
    val dato: LocalDate,
    val timer: Double,
    val kommentar: String?,
    val opprettetDato: LocalDateTime = LocalDateTime.now(),
    val sistEndret: LocalDateTime = LocalDateTime.now(),
    val version: Int = 1
)
