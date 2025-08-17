package com.example.basespringbootapikotlin.config

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header

import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.hamcrest.Matchers.*

@SpringBootTest
@AutoConfigureMockMvc
class CorrelationIdFilterIT @Autowired constructor(
    val mockMvc: MockMvc
) {
    @Test
    fun `should add X-Correlation-Id header if not present`() {
        mockMvc.get("/actuator/health")
            .andExpect {
                header().exists("X-Correlation-Id")
            }
    }

    @Test
    fun `should preserve X-Correlation-Id header if present`() {
        mockMvc.get("/actuator/health") {
            header("X-Correlation-Id", "integration-corr-id")
        }.andExpect {
            header().string("X-Correlation-Id", "integration-corr-id")
        }
    }

    @Test
    fun `should propagate correlationId to MDC and return it from endpoint`() {
        val mvcResult = mockMvc.get("/test/correlation-id")
            .andExpect {
                header().exists("X-Correlation-Id")
                jsonPath("$.mdc", notNullValue())
            }
            .andReturn()
        val json = mvcResult.response.contentAsString
        // Optionally, check that the header and mdc values are equal and a valid UUID
    val objectMapper = com.fasterxml.jackson.module.kotlin.jacksonObjectMapper()
    val node = objectMapper.readTree(json)
    val mdcValue = node.get("mdc")?.asText()
    val uuidRegex = """[0-9a-fA-F-]{36}""".toRegex()
    assert(mdcValue != null && uuidRegex.matches(mdcValue))
    }

    @Test
    fun `should preserve provided correlationId in MDC`() {
        val testId = "integration-corr-id-mdc"
        mockMvc.get("/test/correlation-id") {
            header("X-Correlation-Id", testId)
        }.andExpect {
            header().string("X-Correlation-Id", testId)
            jsonPath("$.mdc", equalTo(testId))
        }
    }
}
