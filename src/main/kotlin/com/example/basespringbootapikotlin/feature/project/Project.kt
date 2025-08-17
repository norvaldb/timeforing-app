package com.example.basespringbootapikotlin.feature.project

import java.time.LocalDateTime

data class Project(
    val projectId: Long = 0,
    val userSub: String,
    val navn: String,
    val beskrivelse: String? = null,
    val aktiv: Boolean = true,
    val opprettetDato: LocalDateTime = LocalDateTime.now(),
    val endretDato: LocalDateTime = LocalDateTime.now()
)
