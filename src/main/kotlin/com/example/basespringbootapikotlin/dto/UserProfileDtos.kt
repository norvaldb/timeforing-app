package com.example.basespringbootapikotlin.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

@Schema(description = "Request for registering a new user")
data class RegisterUserRequest(
    @field:NotBlank(message = "Navn må fylles ut")
    @Schema(description = "Fullt navn", example = "Ola Nordmann")
    val navn: String,

    @field:NotBlank(message = "Mobilnummer må fylles ut")
    @field:Pattern(regexp = "^\+47\d{8}$", message = "Mobilnummer må være på norsk format (+47XXXXXXXX)")
    @Schema(description = "Mobilnummer i norsk format", example = "+4712345678")
    val mobil: String,

    @field:NotBlank(message = "E-post må fylles ut")
    @field:Email(message = "Ugyldig e-postadresse")
    @Schema(description = "E-postadresse", example = "ola@nordmann.no")
    val email: String
)

@Schema(description = "Brukerprofil")
data class UserProfileDto(
    @Schema(description = "Bruker-ID", example = "1")
    val id: Long,
    @Schema(description = "Fullt navn", example = "Ola Nordmann")
    val navn: String,
    @Schema(description = "Mobilnummer", example = "+4712345678")
    val mobil: String,
    @Schema(description = "E-postadresse", example = "ola@nordmann.no")
    val email: String
)

@Schema(description = "Request for updating user profile")
data class UpdateUserProfileRequest(
    @field:NotBlank(message = "Navn må fylles ut")
    @Schema(description = "Fullt navn", example = "Ola Nordmann")
    val navn: String,

    @field:NotBlank(message = "Mobilnummer må fylles ut")
    @field:Pattern(regexp = "^\+47\d{8}$", message = "Mobilnummer må være på norsk format (+47XXXXXXXX)")
    @Schema(description = "Mobilnummer i norsk format", example = "+4712345678")
    val mobil: String
)
