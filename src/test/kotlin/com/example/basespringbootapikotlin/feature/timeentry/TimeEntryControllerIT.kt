package com.example.basespringbootapikotlin.feature.timeentry

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDate
import com.example.basespringbootapikotlin.config.OracleTestContainerConfig
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest(classes = [com.example.basespringbootapikotlin.config.TestSecurityConfig::class])
@AutoConfigureMockMvc
class TimeEntryControllerIT @Autowired constructor(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper,
    @Autowired private val jdbcTemplate: JdbcTemplate
) : OracleTestContainerConfig() {
    private val baseUrl = "/api/time-entries"
        private val userSub = "test-user-sub"
    private val projectId = 1L
    private val today = LocalDate.now()

    @BeforeEach
    fun setup() {
        jdbcTemplate.execute("TRUNCATE TABLE time_entries")
        jdbcTemplate.execute("TRUNCATE TABLE projects")
        jdbcTemplate.update("INSERT INTO projects (project_id, user_sub, navn, aktiv, opprettet_dato, endret_dato) VALUES (?, ?, ?, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
            projectId, userSub, "Testprosjekt")
    }

    @Test
    fun create_and_fetch_time_entry() {
        val dto = mapOf(
            "prosjektId" to projectId,
            "dato" to today.toString(),
            "timer" to 2.0,
            "kommentar" to "Test"
        )
        val json = objectMapper.writeValueAsString(dto)
        val mvcResult = mockMvc.perform(post(baseUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer dummy-jwt-with-sub-claim")
            .content(json))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.timer").value(2.0))
            .andReturn()
        val id = objectMapper.readTree(mvcResult.response.contentAsString).get("timeEntryId").asLong()
        // Fetch
        mockMvc.perform(get("$baseUrl/$id")
            .header("Authorization", "Bearer dummy-jwt-with-sub-claim"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.timer").value(2.0))
    }

    @Test
    fun should_not_allow_more_than_24_hours_per_day() {
        // Create 1st entry
        val dto1 = mapOf("prosjektId" to projectId, "dato" to today.toString(), "timer" to 23.0)
        val dto2 = mapOf("prosjektId" to projectId, "dato" to today.toString(), "timer" to 2.0)
        mockMvc.perform(post(baseUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer dummy-jwt-with-sub-claim")
            .content(objectMapper.writeValueAsString(dto1)))
            .andExpect(status().isCreated)
        // 2nd entry should fail
        mockMvc.perform(post(baseUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer dummy-jwt-with-sub-claim")
            .content(objectMapper.writeValueAsString(dto2)))
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.code").value("MAX_HOURS_PER_DAY"))
    }

    @Test
    fun should_not_allow_non_half_or_whole_hours() {
        val dto = mapOf("prosjektId" to projectId, "dato" to today.toString(), "timer" to 1.3)
        mockMvc.perform(post(baseUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer dummy-jwt-with-sub-claim")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value("INVALID_TIMER_STEP"))
    }

    @Test
    fun `should not allow time entry on inactive or other user's project`() {
        val otherProjectId = 2L
        val dto = mapOf("prosjektId" to otherProjectId, "dato" to today.toString(), "timer" to 2.0)
        mockMvc.perform(post(baseUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer dummy-jwt-with-sub-claim")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value("PROJECT_NOT_FOUND_OR_INACTIVE"))
    }
}
