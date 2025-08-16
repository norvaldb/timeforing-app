package com.example.basespringbootapikotlin.controller

import com.example.basespringbootapikotlin.dto.*
import com.example.basespringbootapikotlin.facade.ProjectFacade
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/projects")
@Tag(name = "Project Management")
class ProjectController(private val projectFacade: ProjectFacade) {

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Opprett nytt prosjekt")
    fun createProject(
        @AuthenticationPrincipal principal: org.springframework.security.core.userdetails.User,
        @Valid @RequestBody request: CreateProjectRequest
    ): ResponseEntity<ProjectDto> {
        val userId = principal.username.toLong() // Assuming username is userId
        val project = projectFacade.createProject(userId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(project)
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Hent alle aktive prosjekter for innlogget bruker")
    fun listProjects(
        @AuthenticationPrincipal principal: org.springframework.security.core.userdetails.User,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") pageSize: Int,
        @RequestParam(defaultValue = "navn") sort: String,
        @RequestParam(defaultValue = "true") asc: Boolean
    ): ResponseEntity<ProjectListResponse> {
        val userId = principal.username.toLong()
        val result = projectFacade.listProjects(userId, page, pageSize, sort, asc)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Hent detaljer for et spesifikt prosjekt")
    fun getProject(
        @AuthenticationPrincipal principal: org.springframework.security.core.userdetails.User,
        @PathVariable id: Long
    ): ResponseEntity<ProjectDto> {
        val userId = principal.username.toLong()
        val project = projectFacade.getProject(userId, id)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(project)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Oppdater prosjekt informasjon")
    fun updateProject(
        @AuthenticationPrincipal principal: org.springframework.security.core.userdetails.User,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateProjectRequest
    ): ResponseEntity<ProjectDto> {
        val userId = principal.username.toLong()
        val updated = projectFacade.updateProject(userId, id, request)
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Soft delete av prosjekt")
    fun deleteProject(
        @AuthenticationPrincipal principal: org.springframework.security.core.userdetails.User,
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        val userId = principal.username.toLong()
        projectFacade.deleteProject(userId, id)
        return ResponseEntity.noContent().build()
    }
}
