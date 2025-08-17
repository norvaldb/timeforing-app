package com.example.basespringbootapikotlin.feature.report

import com.example.basespringbootapikotlin.feature.timeentry.TimeEntry
import com.example.basespringbootapikotlin.feature.timeentry.TimeEntryRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ReportServiceTest {
    private val repository = mockk<TimeEntryRepository>()
    private val service = ReportService(repository)

    @Test
    fun `generateExcel returns non-empty bytes`() {
        val entries = listOf(
            TimeEntry(1, 1, "test-user-sub", LocalDate.of(2025,8,17), 4.0, "jobb", java.time.LocalDateTime.now(), java.time.LocalDateTime.now(), 0),
            TimeEntry(2, 1, "test-user-sub", LocalDate.of(2025,8,16), 2.5, "møte", java.time.LocalDateTime.now(), java.time.LocalDateTime.now(), 0)
        )
        every { repository.findAllByUserSub("test-user-sub", any(), any()) } returns entries

        val bytes = service.generateExcel("test-user-sub", null, null, null)
        assertTrue(bytes.isNotEmpty())
    }
}
