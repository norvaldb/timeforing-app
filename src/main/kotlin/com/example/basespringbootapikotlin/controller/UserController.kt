package com.example.basespringbootapikotlin.controller

import com.example.basespringbootapikotlin.dto.*
import com.example.basespringbootapikotlin.facade.UserFacade
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "API for managing users with Norwegian field names")
class UserController(private val userFacade: UserFacade) {
    
    @PostMapping("/register")
    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account with Norwegian field validation"
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "User created successfully"),
        ApiResponse(responseCode = "400", description = "Invalid input data"),
        ApiResponse(responseCode = "409", description = "Email already exists")
    ])
    fun registerUser(@Valid @RequestBody request: CreateUserRequest): ResponseEntity<UserDto> {
        return try {
            val user = userFacade.createUser(request)
            ResponseEntity.status(HttpStatus.CREATED).body(user)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
    }
    
    @GetMapping("/profile")
    @Operation(
        summary = "Get current user profile",
        description = "Retrieves the authenticated user's profile information"
    )
    @PreAuthorize("hasRole('USER')")
    fun getCurrentUserProfile(): ResponseEntity<UserDto> {
        // TODO: Get user ID from JWT token
        val userId = 1L // Placeholder - should come from authentication context
        
        return userFacade.findUserById(userId)?.let { user ->
            ResponseEntity.ok(user)
        } ?: ResponseEntity.notFound().build()
    }
    
    @PutMapping("/profile")
    @Operation(
        summary = "Update current user profile",
        description = "Updates the authenticated user's profile information"
    )
    @PreAuthorize("hasRole('USER')")
    fun updateCurrentUserProfile(@Valid @RequestBody request: UpdateUserRequest): ResponseEntity<UserDto> {
        return try {
            // TODO: Get user ID from JWT token
            val userId = 1L // Placeholder - should come from authentication context
            
            val updatedUser = userFacade.updateUser(userId, request)
            ResponseEntity.ok(updatedUser)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }
    
    @DeleteMapping("/profile")
    @Operation(
        summary = "Delete current user account",
        description = "Deletes the authenticated user's account permanently"
    )
    @PreAuthorize("hasRole('USER')")
    fun deleteCurrentUserAccount(): ResponseEntity<Void> {
        // TODO: Get user ID from JWT token
        val userId = 1L // Placeholder - should come from authentication context
        
        return if (userFacade.deleteUser(userId)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @GetMapping("/{id}")
    @Operation(
        summary = "Get user by ID",
        description = "Retrieves a user by their ID (admin only)"
    )
    @PreAuthorize("hasRole('ADMIN')")
    fun getUserById(
        @Parameter(description = "User ID", example = "1")
        @PathVariable id: Long
    ): ResponseEntity<UserDto> {
        return userFacade.findUserById(id)?.let { user ->
            ResponseEntity.ok(user)
        } ?: ResponseEntity.notFound().build()
    }
    
    @GetMapping
    @Operation(
        summary = "Get all users",
        description = "Retrieves a paginated list of all users (admin only)"
    )
    @PreAuthorize("hasRole('ADMIN')")
    fun getAllUsers(
        @Parameter(description = "Number of users to return", example = "20")
        @RequestParam(defaultValue = "20") limit: Int,
        @Parameter(description = "Number of users to skip", example = "0")
        @RequestParam(defaultValue = "0") offset: Int
    ): ResponseEntity<List<UserDto>> {
        val users = userFacade.getAllUsers(limit, offset)
        return ResponseEntity.ok(users)
    }
    
    @GetMapping("/check-email")
    @Operation(
        summary = "Check email availability",
        description = "Checks if an email address is available for registration"
    )
    fun checkEmailAvailability(
        @Parameter(description = "Email to check", example = "test@example.com")
        @RequestParam email: String
    ): ResponseEntity<EmailAvailabilityResponse> {
        val available = userFacade.isEmailAvailable(email)
        return ResponseEntity.ok(EmailAvailabilityResponse(available))
    }
}
