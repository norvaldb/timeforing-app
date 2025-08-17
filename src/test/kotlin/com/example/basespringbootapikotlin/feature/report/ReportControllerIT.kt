package com.example.basespringbootapikotlin.feature.report

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import com.example.basespringbootapikotlin.config.TestSecurityConfig
import org.springframework.jdbc.core.JdbcTemplate
import com.example.basespringbootapikotlin.config.OracleTestContainerConfig
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(classes = [TestSecurityConfig::class])
@AutoConfigureMockMvc
class ReportControllerIT @Autowired constructor(
    private val mockMvc: MockMvc,
    @Autowired private val jdbcTemplate: JdbcTemplate
) : com.example.basespringbootapikotlin.config.BaseTransactionalIT() {

    @BeforeEach
    fun setup() {
        // Clean and seed minimal data required for report. Use DELETE so the transaction can rollback.
        jdbcTemplate.execute("DELETE FROM time_entries")
        jdbcTemplate.execute("DELETE FROM projects")
        // Insert a project owned by test-user-sub
        jdbcTemplate.update("INSERT INTO projects (project_id, navn, beskrivelse, user_sub, aktiv) VALUES (?, ?, ?, ?, ?)", 1, "Testproj", "desc", "test-user-sub", 1)
        // Insert a time entry for that project and user
        jdbcTemplate.update("INSERT INTO time_entries (time_entry_id, prosjekt_id, user_sub, dato, timer, kommentar, opprettet_dato, sist_endret, version) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, ?)", 1, 1, "test-user-sub", java.sql.Date.valueOf("2025-08-17"), 4.0, "jobb", 0)
    }
    @Test
    fun `excel endpoint returns xlsx with correct media type`() {
        mockMvc.perform(
            get("/api/reports/excel").header("Authorization", "Bearer dummy-jwt-with-sub-claim")
        )
            .andExpect(status().isOk)
            .andExpect(header().string("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .andExpect { mvcResult ->
                val bytes = mvcResult.response.contentAsByteArray
                org.junit.jupiter.api.Assertions.assertTrue(bytes.isNotEmpty())
            }
    }
}
