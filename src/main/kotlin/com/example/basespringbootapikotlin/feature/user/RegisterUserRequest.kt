package com.example.basespringbootapikotlin.feature.user

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

/**
 * DTO for user registration
 */
data class RegisterUserRequest(
    @field:NotBlank(message = "Navn er påkrevd")
    @field:Size(min = 2, max = 100, message = "Navn må være mellom 2 og 100 tegn")
    val navn: String,

    @field:NotBlank(message = "Mobilnummer er påkrevd")
    @field:Pattern(regexp = "^\\+47\\d{8}$", message = "Mobilnummer må være på norsk format (+47XXXXXXXX)")
    val mobil: String,

    @field:NotBlank(message = "E-post er påkrevd")
    @field:Email(message = "Ugyldig e-postadresse")
    val epost: String
)
