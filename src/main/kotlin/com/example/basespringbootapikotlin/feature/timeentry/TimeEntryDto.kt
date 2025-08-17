package com.example.basespringbootapikotlin.feature.timeentry

import java.time.LocalDate
import java.time.LocalDateTime

data class TimeEntryDto(
    val timeEntryId: Long?,
    val prosjektId: Long,
    val dato: LocalDate,
    val timer: Double,
    val kommentar: String?,
    val opprettetDato: LocalDateTime?,
    val sistEndret: LocalDateTime?
)

fun TimeEntry.toDto() = TimeEntryDto(
    timeEntryId = timeEntryId,
    prosjektId = prosjektId,
    dato = dato,
    timer = timer,
    kommentar = kommentar,
    opprettetDato = opprettetDato,
    sistEndret = sistEndret
)
