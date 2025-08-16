package com.example.basespringbootapikotlin.facade

import com.example.basespringbootapikotlin.dto.*
import com.example.basespringbootapikotlin.model.Project
import com.example.basespringbootapikotlin.repository.ProjectRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class ProjectFacadeImpl(
    private val projectRepository: ProjectRepository
) : ProjectFacade {
    override fun createProject(userId: Long, request: CreateProjectRequest): ProjectDto {
        val project = Project(
            userId = userId,
            navn = request.navn.trim(),
            beskrivelse = request.beskrivelse?.trim(),
            aktiv = true,
            opprettetDato = LocalDateTime.now(),
            endretDato = LocalDateTime.now()
        )
        val saved = projectRepository.save(project)
        return saved.toDto()
    }

    override fun getProject(userId: Long, projectId: Long): ProjectDto? {
        return projectRepository.findByIdAndUser(projectId, userId)?.toDto()
    }

    override fun listProjects(userId: Long, page: Int, pageSize: Int, sort: String, asc: Boolean): ProjectListResponse {
        val projects = projectRepository.findAllByUser(userId, page, pageSize, sort, asc)
        val total = projectRepository.countByUser(userId)
        return ProjectListResponse(
            projects = projects.map { it.toDto() },
            page = page,
            pageSize = pageSize,
            total = total
        )
    }

    override fun updateProject(userId: Long, projectId: Long, request: UpdateProjectRequest): ProjectDto {
        val existing = projectRepository.findByIdAndUser(projectId, userId)
            ?: throw IllegalArgumentException("Prosjekt ikke funnet")
        val updated = existing.copy(
            navn = request.navn.trim(),
            beskrivelse = request.beskrivelse?.trim(),
            endretDato = LocalDateTime.now()
        )
        if (!projectRepository.update(updated)) {
            throw IllegalStateException("Kunne ikke oppdatere prosjekt")
        }
        return updated.toDto()
    }

    override fun deleteProject(userId: Long, projectId: Long): Boolean {
        val existing = projectRepository.findByIdAndUser(projectId, userId)
            ?: throw IllegalArgumentException("Prosjekt ikke funnet")
        if (projectRepository.existsWithTimeregistrering(projectId)) {
            throw IllegalStateException("Kan ikke slette prosjekt med registrerte timer")
        }
        return projectRepository.softDelete(projectId, userId)
    }
}

private fun Project.toDto() = ProjectDto(
    projectId = this.projectId,
    navn = this.navn,
    beskrivelse = this.beskrivelse,
    aktiv = this.aktiv,
    opprettetDato = this.opprettetDato,
    endretDato = this.endretDato
)
