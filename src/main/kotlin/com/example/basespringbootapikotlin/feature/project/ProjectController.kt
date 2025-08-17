package com.example.basespringbootapikotlin.feature.project

import com.example.basespringbootapikotlin.feature.project.*
import com.example.basespringbootapikotlin.feature.project.ProjectFacade
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

import org.slf4j.LoggerFactory
import org.slf4j.MDC

@RestController
@RequestMapping("/api/projects")
@Tag(name = "Project Management")
class ProjectController(private val projectFacade: ProjectFacade) {

    private val log = LoggerFactory.getLogger(ProjectController::class.java)

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    @Operation(summary = "Opprett nytt prosjekt")
    fun createProject(
        @AuthenticationPrincipal principal: Jwt,
        @Valid @RequestBody request: CreateProjectRequest
    ): ResponseEntity<ProjectDto> {
        val userSub = principal.claims["sub"] as String
    val correlationId = MDC.get("correlationId")
    log.info("Creating project for userSub={} [correlationId={}]", userSub, correlationId)
        val project = projectFacade.createProject(userSub, request)
    log.info("Project created for userSub={} [correlationId={}]", userSub, correlationId)
        return ResponseEntity.status(HttpStatus.CREATED).body(project)
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    @Operation(summary = "Hent alle aktive prosjekter for innlogget bruker")
    fun listProjects(
        @AuthenticationPrincipal principal: Jwt,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") pageSize: Int,
        @RequestParam(defaultValue = "navn") sort: String,
        @RequestParam(defaultValue = "true") asc: Boolean
    ): ResponseEntity<ProjectListResponse> {
        val userSub = principal.claims["sub"] as String
    val correlationId = MDC.get("correlationId")
    log.info("Listing projects for userSub={} [correlationId={}]", userSub, correlationId)
        val result = projectFacade.listProjects(userSub, page, pageSize, sort, asc)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    @Operation(summary = "Hent detaljer for et spesifikt prosjekt")
    fun getProject(
        @AuthenticationPrincipal principal: Jwt,
        @PathVariable id: Long
    ): ResponseEntity<ProjectDto> {
        val userSub = principal.claims["sub"] as String
    val correlationId = MDC.get("correlationId")
    log.info("Getting project id={} for userSub={} [correlationId={}]", id, userSub, correlationId)
        val project = projectFacade.getProject(userSub, id)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(project)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    @Operation(summary = "Oppdater prosjekt informasjon")
    fun updateProject(
        @AuthenticationPrincipal principal: Jwt,
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateProjectRequest
    ): ResponseEntity<ProjectDto> {
        val userSub = principal.claims["sub"] as String
    val correlationId = MDC.get("correlationId")
    log.info("Updating project id={} for userSub={} [correlationId={}]", id, userSub, correlationId)
        val updated = projectFacade.updateProject(userSub, id, request)
    log.info("Project updated id={} for userSub={} [correlationId={}]", id, userSub, correlationId)
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    @Operation(summary = "Soft delete av prosjekt")
    fun deleteProject(
        @AuthenticationPrincipal principal: Jwt,
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        val userSub = principal.claims["sub"] as String
    val correlationId = MDC.get("correlationId")
    log.info("Deleting project id={} for userSub={} [correlationId={}]", id, userSub, correlationId)
        projectFacade.deleteProject(userSub, id)
    log.info("Project deleted id={} for userSub={} [correlationId={}]", id, userSub, correlationId)
        return ResponseEntity.noContent().build()
    }
}
