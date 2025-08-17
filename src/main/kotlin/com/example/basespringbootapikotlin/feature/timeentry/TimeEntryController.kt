package com.example.basespringbootapikotlin.feature.timeentry

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/time-entries")
@Tag(name = "Timeføring")
class TimeEntryController(private val facade: TimeEntryFacade) {
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    @Operation(summary = "Registrer tid på prosjekt")
    fun create(
        @AuthenticationPrincipal principal: Jwt,
        @RequestBody dto: TimeEntryDto
    ): ResponseEntity<TimeEntryDto> {
        val userSub = principal.claims["sub"] as String
        val created = facade.createTimeEntry(userSub, dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    @Operation(summary = "Hent timeføringer for periode")
    fun list(
        @AuthenticationPrincipal principal: Jwt,
        @RequestParam(required = false) from: LocalDate?,
        @RequestParam(required = false) to: LocalDate?
    ): List<TimeEntryDto> {
        val userSub = principal.claims["sub"] as String
        return facade.listTimeEntries(userSub, from, to)
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    @Operation(summary = "Hent spesifikk timeføring")
    fun get(
        @AuthenticationPrincipal principal: Jwt,
        @PathVariable id: Long
    ): ResponseEntity<TimeEntryDto> {
        val userSub = principal.claims["sub"] as String
        val entry = facade.getTimeEntry(userSub, id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(entry)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    @Operation(summary = "Oppdater timeføring")
    fun update(
        @AuthenticationPrincipal principal: Jwt,
        @PathVariable id: Long,
        @RequestBody dto: TimeEntryDto
    ): ResponseEntity<TimeEntryDto> {
        val userSub = principal.claims["sub"] as String
        val updated = facade.updateTimeEntry(userSub, id, dto)
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    @Operation(summary = "Slett timeføring")
    fun delete(
        @AuthenticationPrincipal principal: Jwt,
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        val userSub = principal.claims["sub"] as String
        facade.deleteTimeEntry(userSub, id)
        return ResponseEntity.noContent().build()
    }
}
