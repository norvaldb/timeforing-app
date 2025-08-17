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
    fun `should return a JWT token for valid request`() {
        val url = "http://localhost:$port/api/mock-auth/token"
        val request = MockAuthController.AuthRequest(username = "ituser", roles = listOf("USER", "ADMIN"))
        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }
        val entity = HttpEntity(request, headers)
        val response = restTemplate.postForEntity(url, entity, MockAuthController.AuthResponse::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.token).isNotNull()
        assertThat(response.body?.token).isNotEmpty()
    }
}
