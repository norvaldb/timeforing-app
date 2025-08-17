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

    fun generateToken(username: String, roles: List<String>, validHours: Long = 12): String {
        val now = Instant.now()
        val expiry = now.plusSeconds(validHours * 60 * 60)
        return Jwts.builder()
            .subject(username)
            .issuer(issuer)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiry))
            .claim("roles", roles)
            .signWith(secretKey, io.jsonwebtoken.SignatureAlgorithm.HS256)
            .compact()
    }

    fun getIssuer(): String = issuer
    fun getSecretKey(): Key = secretKey
}
