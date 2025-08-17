package com.example.basespringbootapikotlin.feature.mockauth

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.*
import org.assertj.core.api.Assertions.assertThat

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MockAuthControllerIT {
    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @LocalServerPort
    var port: Int = 0

    @Test
    fun `should return a JWT token for valid request with all claims`() {
        val url = "http://localhost:$port/api/mock-auth/token"
        val request = MockAuthController.AuthRequest(
            sub = "ituser123",
            name = "Integration Test User",
            email = "ituser@example.com",
            phone = "+4711223344",
            roles = listOf("timeforingapp::USER", "timeforingapp::ADMIN")
        )
        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }
        val entity = HttpEntity(request, headers)
        val response = restTemplate.postForEntity(url, entity, MockAuthController.AuthResponse::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.token).isNotNull()
        assertThat(response.body?.token).isNotEmpty()
        // Optionally: decode and check claims
        val token = response.body?.token!!
        val payload = String(java.util.Base64.getUrlDecoder().decode(token.split(".")[1]))
        assertThat(payload).contains("ituser123")
        assertThat(payload).contains("Integration Test User")
        assertThat(payload).contains("ituser@example.com")
        assertThat(payload).contains("+4711223344")
        assertThat(payload).contains("timeforingapp::USER")
    }
}
