package com.example.basespringbootapikotlin.config

import org.mockito.Mockito
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder

@TestConfiguration
class TestSecurityConfig {
    @Bean
    @Primary
    fun jwtDecoder(): JwtDecoder = Mockito.mock(JwtDecoder::class.java).apply {
        Mockito.`when`(decode(Mockito.anyString())).thenAnswer { invocation ->
            val token = invocation.arguments[0] as String
            val userSub = when {
                token.contains("different-sub") -> "different-user-sub"
                else -> "test-user-sub"
            }
            Jwt.withTokenValue(token)
                .header("alg", "none")
                .claim("sub", userSub)
                .claim("scope", "USER") // Add scope claim for authorities
                .build()
        }
    }
}
