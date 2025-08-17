package com.example.basespringbootapikotlin.feature.project

import com.example.basespringbootapikotlin.feature.project.CreateProjectRequest
import com.example.basespringbootapikotlin.feature.project.UpdateProjectRequest
import com.example.basespringbootapikotlin.feature.project.Project
import com.example.basespringbootapikotlin.feature.project.ProjectRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ProjectFacadeTest {
    private lateinit var projectRepository: ProjectRepository
    private lateinit var projectFacade: ProjectFacadeImpl
    private val userSub = "test-user-sub"
    private val now = LocalDateTime.now()

    @BeforeEach
    fun setUp() {
        projectRepository = mockk(relaxed = true)
        projectFacade = ProjectFacadeImpl(projectRepository)
    }

    @Test
    fun `createProject should save and return project`() {
        val request = CreateProjectRequest("Prosjekt X", "Beskrivelse")
        val project = Project(10L, userSub, request.navn, request.beskrivelse, true, now, now)
        every { projectRepository.save(any()) } returns project
        val result = projectFacade.createProject(userSub, request)
        assertEquals("Prosjekt X", result.navn)
        assertEquals("Beskrivelse", result.beskrivelse)
        verify { projectRepository.save(any()) }
    }

    @Test
    fun `getProject should return project if found`() {
        val project = Project(10L, userSub, "P1", "B", true, now, now)
    every { projectRepository.findByIdAndUserSub(10L, userSub) } returns project
        val result = projectFacade.getProject(userSub, 10L)
        assertNotNull(result)
        assertEquals("P1", result!!.navn)
    }

    @Test
    fun `getProject should return null if not found`() {
    every { projectRepository.findByIdAndUserSub(99L, userSub) } returns null
        val result = projectFacade.getProject(userSub, 99L)
        assertNull(result)
    }

    @Test
    fun `updateProject should update and return project`() {
        val project = Project(10L, userSub, "Old", "Old desc", true, now, now)
        val update = UpdateProjectRequest("New", "New desc")
    every { projectRepository.findByIdAndUserSub(10L, userSub) } returns project
        every { projectRepository.update(any()) } returns true
        val result = projectFacade.updateProject(userSub, 10L, update)
        assertEquals("New", result.navn)
        assertEquals("New desc", result.beskrivelse)
    }

    @Test
    fun `updateProject should throw if not found`() {
    every { projectRepository.findByIdAndUserSub(10L, userSub) } returns null
        val update = UpdateProjectRequest("New", "New desc")
        assertThrows(IllegalArgumentException::class.java) {
            projectFacade.updateProject(userSub, 10L, update)
        }
    }

    @Test
    fun `deleteProject should soft delete if no time entries`() {
        val project = Project(10L, userSub, "P", "B", true, now, now)
    every { projectRepository.findByIdAndUserSub(10L, userSub) } returns project
        every { projectRepository.existsWithTimeregistrering(10L) } returns false
        every { projectRepository.softDelete(10L, userSub) } returns true
        val result = projectFacade.deleteProject(userSub, 10L)
        assertTrue(result)
    }

    @Test
    fun `deleteProject should throw if project has time entries`() {
        val project = Project(10L, userSub, "P", "B", true, now, now)
    every { projectRepository.findByIdAndUserSub(10L, userSub) } returns project
        every { projectRepository.existsWithTimeregistrering(10L) } returns true
        assertThrows(IllegalStateException::class.java) {
            projectFacade.deleteProject(userSub, 10L)
        }
    }

    @Test
    fun `deleteProject should throw if not found`() {
    every { projectRepository.findByIdAndUserSub(10L, userSub) } returns null
        assertThrows(IllegalArgumentException::class.java) {
            projectFacade.deleteProject(userSub, 10L)
        }
    }
}
