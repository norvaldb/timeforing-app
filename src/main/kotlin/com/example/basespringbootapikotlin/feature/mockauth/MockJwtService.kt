package com.example.basespringbootapikotlin.feature.mockauth

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.Jwts.SIG
import org.springframework.stereotype.Service
import java.security.Key
import java.time.Instant
import java.util.Date

@Service
class MockJwtService {
    private val secretKey: Key = SIG.HS256.key().build()
    private val issuer = "mock-issuer"

    fun generateToken(
        sub: String,
        name: String,
        email: String,
        phone: String? = null,
        roles: List<String>,
        validHours: Long = 12
    ): String {
        val now = Instant.now()
        val expiry = now.plusSeconds(validHours * 60 * 60)
        val builder = Jwts.builder()
            .subject(sub)
            .issuer(issuer)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiry))
            .claim("name", name)
            .claim("email", email)
            .claim("roles", roles)
        if (phone != null) builder.claim("phone", phone)
        return builder
            .signWith(secretKey, io.jsonwebtoken.SignatureAlgorithm.HS256)
            .compact()
    }

    fun getIssuer(): String = issuer
    fun getSecretKey(): Key = secretKey
}
