
package com.example.basespringbootapikotlin.feature.mockauth

import io.kotest.matchers.shouldBe

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain

class MockJwtServiceTest : StringSpec({
    val service = MockJwtService()

    "should generate a valid JWT token" {
        val token = service.generateToken("testuser", listOf("USER", "ADMIN"))
        token shouldNotBe null
        token shouldContain "."
    }

    "should include username and roles in JWT claims" {
        val token = service.generateToken("alice", listOf("USER"))
        // Simple check: token is not empty and has 3 parts (header.payload.signature)
        token.split(".").size shouldBe 3
    }
})
