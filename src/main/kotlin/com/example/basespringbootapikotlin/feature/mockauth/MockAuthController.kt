package com.example.basespringbootapikotlin.feature.mockauth

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank

@Tag(name = "Mock Auth")
@RestController
@RequestMapping("/api/mock-auth")
class MockAuthController(private val mockJwtService: MockJwtService) {
    data class AuthRequest(
        @field:NotBlank val username: String,
        val roles: List<String> = listOf("USER")
    )
    data class AuthResponse(val token: String)

    @Operation(summary = "Get a mock JWT token for local development")
    @PostMapping("/token")
    fun getMockToken(@Valid @RequestBody request: AuthRequest): ResponseEntity<AuthResponse> {
        val token = mockJwtService.generateToken(request.username, request.roles)
        return ResponseEntity.ok(AuthResponse(token))
    }
}
