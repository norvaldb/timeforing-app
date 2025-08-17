package com.example.basespringbootapikotlin.feature.timeentry

import com.example.basespringbootapikotlin.feature.project.Project
import com.example.basespringbootapikotlin.feature.project.ProjectRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

class TimeEntryFacadeTest {
    private lateinit var repository: TimeEntryRepository
    private lateinit var projectRepository: ProjectRepository
    private lateinit var validator: TimeEntryBusinessValidator
    private lateinit var facade: TimeEntryFacadeImpl
    private val userSub = "test-user"
    private val projectId = 1L
    private val today = LocalDate.now()
    private val now = LocalDateTime.now()

    @BeforeEach
    fun setUp() {
        repository = mockk(relaxed = true)
        projectRepository = mockk(relaxed = true)
        validator = TimeEntryBusinessValidator(projectRepository, repository)
        facade = TimeEntryFacadeImpl(repository, validator)
    }

    @Test
    fun should_create_time_entry_with_valid_data() {
        val project = Project(projectId, userSub, "P", "B", true, now, now)
        every { projectRepository.findByIdAndUserSub(projectId, userSub) } returns project
        every { repository.findAllByUserSub(userSub, today, today) } returns emptyList()
        val dto = TimeEntryDto(null, projectId, today, 2.0, "", now, now)
        every { repository.save(any()) } answers { firstArg<TimeEntry>().copy(timeEntryId = 10L) }
        val result = facade.createTimeEntry(userSub, dto)
        assertEquals(2.0, result.timer)
        assertEquals(10L, result.timeEntryId)
    }

    @Test
    fun should_not_allow_more_than_24_hours_per_day() {
        val project = Project(projectId, userSub, "P", "B", true, now, now)
        every { projectRepository.findByIdAndUserSub(projectId, userSub) } returns project
        every { repository.findAllByUserSub(userSub, today, today) } returns listOf(
            TimeEntry(1L, projectId, userSub, today, 23.0, "", now, now, 1)
        )
        val dto = TimeEntryDto(null, projectId, today, 2.0, "", now, now)
        val ex = assertThrows(IllegalArgumentException::class.java) {
            facade.createTimeEntry(userSub, dto)
        }
        assertTrue(ex.message!!.contains("mer enn 24 timer"))
    }

    @Test
    fun should_not_allow_negative_or_zero_hours() {
        val project = Project(projectId, userSub, "P", "B", true, now, now)
        every { projectRepository.findByIdAndUserSub(projectId, userSub) } returns project
        every { repository.findAllByUserSub(userSub, today, today) } returns emptyList()
        val dto = TimeEntryDto(null, projectId, today, 0.0, "", now, now)
        val ex = assertThrows(IllegalArgumentException::class.java) {
            facade.createTimeEntry(userSub, dto)
        }
        assertTrue(ex.message!!.contains("positiv verdi"))
    }

    @Test
    fun should_not_allow_non_half_or_whole_hours() {
        val project = Project(projectId, userSub, "P", "B", true, now, now)
        every { projectRepository.findByIdAndUserSub(projectId, userSub) } returns project
        every { repository.findAllByUserSub(userSub, today, today) } returns emptyList()
        val dto = TimeEntryDto(null, projectId, today, 1.3, "", now, now)
        val ex = assertThrows(IllegalArgumentException::class.java) {
            facade.createTimeEntry(userSub, dto)
        }
        assertTrue(ex.message!!.contains("hele eller halve"))
    }

    @Test
    fun `should not allow time entry on inactive or other user's project`() {
        every { projectRepository.findByIdAndUserSub(projectId, userSub) } returns null
        val dto = TimeEntryDto(null, projectId, today, 2.0, "", now, now)
        val ex = assertThrows(IllegalArgumentException::class.java) {
            facade.createTimeEntry(userSub, dto)
        }
        assertTrue(ex.message!!.contains("Prosjekt ikke funnet"))
    }
}
