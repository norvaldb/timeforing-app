package com.example.basespringbootapikotlin.feature.mockauth

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank

import org.slf4j.LoggerFactory
import org.slf4j.MDC

@Tag(name = "Mock Auth")
@RestController
@RequestMapping("/api/mock-auth")
class MockAuthController(private val mockJwtService: MockJwtService) {
    private val log = LoggerFactory.getLogger(MockAuthController::class.java)
    data class AuthRequest(
        @field:NotBlank val sub: String, // stable user id
        @field:NotBlank val name: String,
        @field:NotBlank val email: String,
        val phone: String? = null,
        val roles: List<String> = listOf("timeforingapp::USER")
    )
    data class AuthResponse(val token: String)

    @Operation(summary = "Get a mock JWT token for local development")
    @PostMapping("/token")
    fun getMockToken(@Valid @RequestBody request: AuthRequest): ResponseEntity<AuthResponse> {
    val correlationId = MDC.get("correlationId")
    log.info("Generating mock JWT for sub={} [correlationId={}]", request.sub, correlationId)
        val token = mockJwtService.generateToken(
            sub = request.sub,
            name = request.name,
            email = request.email,
            phone = request.phone,
            roles = request.roles
        )
    log.info("Mock JWT generated for sub={} [correlationId={}]", request.sub, correlationId)
        return ResponseEntity.ok(AuthResponse(token))
    }
}
