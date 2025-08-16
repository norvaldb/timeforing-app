package com.example.basespringbootapikotlin.controller

import com.example.basespringbootapikotlin.dto.*
import com.example.basespringbootapikotlin.facade.UserFacade
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(private val userFacade: UserFacade) {

    @PostMapping("/register")
    fun registerUser(@Valid @RequestBody request: RegisterUserRequest): ResponseEntity<UserProfileDto> {
        return try {
            val user = userFacade.registerUser(request)
            ResponseEntity.status(HttpStatus.CREATED).body(user)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    fun getCurrentUserProfile(): ResponseEntity<UserProfileDto> {
        val userId = getCurrentUserId()
        return userFacade.getProfile(userId)?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    fun updateCurrentUserProfile(@Valid @RequestBody request: UpdateUserProfileRequest): ResponseEntity<UserProfileDto> {
        return try {
            val userId = getCurrentUserId()
            val updatedUser = userFacade.updateProfile(userId, request)
            ResponseEntity.ok(updatedUser)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @DeleteMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    fun deleteCurrentUserAccount(): ResponseEntity<Void> {
        val userId = getCurrentUserId()
        return if (userFacade.deleteProfile(userId)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/check-email")
    fun checkEmailAvailability(@RequestParam epost: String): ResponseEntity<EmailAvailabilityResponse> {
        val available = userFacade.isEmailAvailable(epost)
        return ResponseEntity.ok(EmailAvailabilityResponse(available))
    }

    private fun getCurrentUserId(): Long {
        val authentication = SecurityContextHolder.getContext().authentication
        val principal = authentication.principal
        if (principal is Jwt) {
            return (principal.claims["user_id"] as? Number)?.toLong()
                ?: throw IllegalStateException("user_id claim mangler i token")
        }
        throw IllegalStateException("Ugyldig authentication principal")
    }
}
