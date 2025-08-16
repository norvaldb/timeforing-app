package com.example.basespringbootapikotlin.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class CreateProjectRequest(
    @field:NotBlank(message = "Prosjektnavn er påkrevd")
    @field:Size(min = 2, message = "Prosjektnavn må være minst 2 tegn")
    val navn: String,
    @field:Size(max = 500, message = "Beskrivelse kan være maks 500 tegn")
    val beskrivelse: String? = null
)

data class UpdateProjectRequest(
    @field:NotBlank(message = "Prosjektnavn er påkrevd")
    @field:Size(min = 2, message = "Prosjektnavn må være minst 2 tegn")
    val navn: String,
    @field:Size(max = 500, message = "Beskrivelse kan være maks 500 tegn")
    val beskrivelse: String? = null
)

data class ProjectDto(
    val projectId: Long,
    val navn: String,
    val beskrivelse: String?,
    val aktiv: Boolean,
    val opprettetDato: LocalDateTime,
    val endretDato: LocalDateTime
)

data class ProjectListResponse(
    val projects: List<ProjectDto>,
    val page: Int,
    val pageSize: Int,
    val total: Long
)
