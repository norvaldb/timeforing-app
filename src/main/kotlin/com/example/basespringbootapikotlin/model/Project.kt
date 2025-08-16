package com.example.basespringbootapikotlin.model

import java.time.LocalDateTime

data class Project(
    val projectId: Long = 0,
    val userId: Long,
    val navn: String,
    val beskrivelse: String? = null,
    val aktiv: Boolean = true,
    val opprettetDato: LocalDateTime = LocalDateTime.now(),
    val endretDato: LocalDateTime = LocalDateTime.now()
)
