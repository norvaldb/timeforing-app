package com.example.basespringbootapikotlin.feature.user

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

/**
 * User registration request DTO
 */
@Schema(description = "Request for creating a new user")
data class CreateUserRequest(
    @field:NotBlank(message = "Navn er påkrevd")
    @field:Size(min = 2, max = 100, message = "Navn må være mellom 2 og 100 tegn")
    @Schema(description = "User's full name", example = "Ola Nordmann")
    val navn: String,
    
    @field:NotBlank(message = "Mobilnummer er påkrevd")
    @field:Pattern(
        regexp = "^(\\+47|0047|47)?[4-9]\\d{7}$",
        message = "Ugyldig norsk mobilnummer"
    )
    @Schema(description = "Norwegian mobile number", example = "+47 41234567")
    val mobil: String,
    
    @field:NotBlank(message = "Epost er påkrevd")
    @field:Email(message = "Ugyldig epost adresse")
    @field:Size(max = 255, message = "Epost kan ikke være lengre enn 255 tegn")
    @Schema(description = "Email address", example = "ola.nordmann@example.com")
    val epost: String
)

/**
 * User profile update request DTO
 */
@Schema(description = "Request for updating user profile")
data class UpdateUserRequest(
    @field:Size(min = 2, max = 100, message = "Navn må være mellom 2 og 100 tegn")
    @Schema(description = "User's full name", example = "Ola Nordmann")
    val navn: String? = null,
    
    @field:Pattern(
        regexp = "^(\\+47|0047|47)?[4-9]\\d{7}$",
        message = "Ugyldig norsk mobilnummer"
    )
    @Schema(description = "Norwegian mobile number", example = "+47 41234567")
    val mobil: String? = null,
    
    @field:Email(message = "Ugyldig epost adresse")
    @field:Size(max = 255, message = "Epost kan ikke være lengre enn 255 tegn")
    @Schema(description = "Email address", example = "ola.nordmann@example.com")
    val epost: String? = null
)

/**
 * User response DTO
 */
@Schema(description = "User information response")
data class UserDto(
    @Schema(description = "User ID", example = "1")
    val id: String,
    
    @Schema(description = "User's full name", example = "Ola Nordmann")
    val navn: String,
    
    @Schema(description = "Norwegian mobile number", example = "+47 41234567")
    val mobil: String,
    
    @Schema(description = "Email address", example = "ola.nordmann@example.com")
    val epost: String,
    
    @Schema(description = "Account creation date")
    val createdAt: String,
    
    @Schema(description = "Last update date")
    val updatedAt: String
)

/**
 * Email availability check response
 */
@Schema(description = "Email availability check result")
data class EmailAvailabilityResponse(
    @Schema(description = "Whether the email is available", example = "true")
    val available: Boolean
)
