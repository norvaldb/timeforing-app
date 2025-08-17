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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.nio.file.Files
import java.nio.file.Paths
import org.apache.poi.xssf.usermodel.XSSFWorkbook

@SpringBootTest(classes = [TestSecurityConfig::class])
@AutoConfigureMockMvc
class ReportControllerSaveExcelIT @Autowired constructor(
    private val mockMvc: MockMvc,
    @Autowired private val jdbcTemplate: JdbcTemplate
) : com.example.basespringbootapikotlin.config.BaseTransactionalIT() {

    @BeforeEach
    fun setup() {
        // Clean and seed minimal data required for report. Use DELETE so the transaction can rollback.
        jdbcTemplate.execute("DELETE FROM time_entries")
        jdbcTemplate.execute("DELETE FROM projects")
    // Insert several projects owned by test-user-sub with realistic names
    jdbcTemplate.update("INSERT INTO projects (project_id, navn, beskrivelse, user_sub, aktiv) VALUES (?, ?, ?, ?, ?)", 101, "Kundeportal - Integrasjon", "Integration work for Kundeportal", "test-user-sub", 1)
    jdbcTemplate.update("INSERT INTO projects (project_id, navn, beskrivelse, user_sub, aktiv) VALUES (?, ?, ?, ?, ?)", 102, "Intern verktøy", "Internal tooling and scripts", "test-user-sub", 1)
    jdbcTemplate.update("INSERT INTO projects (project_id, navn, beskrivelse, user_sub, aktiv) VALUES (?, ?, ?, ?, ?)", 103, "Konsulentoppdrag", "Consulting hours for external client", "test-user-sub", 1)

    // Insert multiple time entries across different dates and projects
    jdbcTemplate.update("INSERT INTO time_entries (time_entry_id, prosjekt_id, user_sub, dato, timer, kommentar, opprettet_dato, sist_endret, version) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, ?)", 201, 101, "test-user-sub", java.sql.Date.valueOf("2025-08-11"), 4.0, "Feature work", 0)
    jdbcTemplate.update("INSERT INTO time_entries (time_entry_id, prosjekt_id, user_sub, dato, timer, kommentar, opprettet_dato, sist_endret, version) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, ?)", 202, 101, "test-user-sub", java.sql.Date.valueOf("2025-08-12"), 3.5, "Bugfix", 0)
    jdbcTemplate.update("INSERT INTO time_entries (time_entry_id, prosjekt_id, user_sub, dato, timer, kommentar, opprettet_dato, sist_endret, version) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, ?)", 203, 102, "test-user-sub", java.sql.Date.valueOf("2025-08-13"), 2.0, "CI improvements", 0)
    jdbcTemplate.update("INSERT INTO time_entries (time_entry_id, prosjekt_id, user_sub, dato, timer, kommentar, opprettet_dato, sist_endret, version) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, ?)", 204, 103, "test-user-sub", java.sql.Date.valueOf("2025-08-14"), 6.0, "Onsite consulting", 0)
    jdbcTemplate.update("INSERT INTO time_entries (time_entry_id, prosjekt_id, user_sub, dato, timer, kommentar, opprettet_dato, sist_endret, version) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, ?)", 205, 103, "test-user-sub", java.sql.Date.valueOf("2025-08-15"), 4.0, "Follow-up", 0)
    jdbcTemplate.update("INSERT INTO time_entries (time_entry_id, prosjekt_id, user_sub, dato, timer, kommentar, opprettet_dato, sist_endret, version) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, ?)", 206, 101, "test-user-sub", java.sql.Date.valueOf("2025-08-17"), 2.5, "Review", 0)
    }

    @Test
    fun `save excel report to target reports folder`() {
        val mvcResult = mockMvc.perform(
            get("/api/reports/excel").header("Authorization", "Bearer dummy-jwt-with-sub-claim")
        )
            .andExpect(status().isOk)
            .andReturn()

        val bytes = mvcResult.response.contentAsByteArray
        val dir = Paths.get("target", "reports")
        Files.createDirectories(dir)
        val filename = "report-${System.currentTimeMillis()}.xlsx"
        val path = dir.resolve(filename)
        Files.write(path, bytes)

        org.junit.jupiter.api.Assertions.assertTrue(Files.exists(path))
        org.junit.jupiter.api.Assertions.assertTrue(Files.size(path) > 0)
        println("Saved report to: ${path.toAbsolutePath()}")

        // Open workbook and assert reporter info and header styling
        val wb = XSSFWorkbook(Files.newInputStream(path))
        val sheet = wb.getSheetAt(0)
        val reporterCell = sheet.getRow(0).getCell(0).stringCellValue
        org.junit.jupiter.api.Assertions.assertTrue(reporterCell.contains("test-user-sub") || reporterCell.contains("Rapportør"))

        val headerRow = sheet.getRow(2)
        // header should have bold font (we check that the first header cell has a font and that it's bold)
        val workbookFontIndex = headerRow.getCell(0).cellStyle?.fontIndex?.toInt()
        if (workbookFontIndex != null) {
            val font = wb.getFontAt(workbookFontIndex)
            org.junit.jupiter.api.Assertions.assertTrue(font.bold)
        }
        wb.close()
    }
}
