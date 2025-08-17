package com.example.basespringbootapikotlin.feature.user

import java.time.LocalDateTime

/**
 * User entity representing the Norwegian database schema
 */
data class User(
    val userId: Long = 0,
    val navn: String,
    val mobil: String,
    val epost: String,
    val status: UserStatus = UserStatus.ACTIVE,
    val opprettetDato: LocalDateTime = LocalDateTime.now(),
    val sistEndret: LocalDateTime = LocalDateTime.now(),
    val version: Long = 1
)

enum class UserStatus {
    ACTIVE,
    PENDING,
    SUSPENDED
}
