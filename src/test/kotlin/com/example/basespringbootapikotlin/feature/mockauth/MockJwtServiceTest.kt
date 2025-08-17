
package com.example.basespringbootapikotlin.feature.mockauth

import io.kotest.matchers.shouldBe

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain

class MockJwtServiceTest : StringSpec({
    val service = MockJwtService()

    "should generate a valid JWT token with all claims" {
        val token = service.generateToken(
            sub = "abc123",
            name = "Ola Nordmann",
            email = "ola@nordmann.no",
            phone = "+4712345678",
            roles = listOf("timeforingapp::USER", "timeforingapp::ADMIN")
        )
        token shouldNotBe null
        token shouldContain "."
        token.split(".").size shouldBe 3
        // Optionally: decode and check claims (base64 decode payload)
        val payload = String(java.util.Base64.getUrlDecoder().decode(token.split(".")[1]))
        payload shouldContain "abc123"
        payload shouldContain "Ola Nordmann"
        payload shouldContain "ola@nordmann.no"
        payload shouldContain "+4712345678"
        payload shouldContain "timeforingapp::USER"
    }
})
