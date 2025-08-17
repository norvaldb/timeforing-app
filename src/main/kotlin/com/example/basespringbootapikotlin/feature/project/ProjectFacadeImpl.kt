package com.example.basespringbootapikotlin.feature.project

import com.example.basespringbootapikotlin.feature.project.*
import com.example.basespringbootapikotlin.feature.project.Project
import com.example.basespringbootapikotlin.feature.project.ProjectRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class ProjectFacadeImpl(
    private val projectRepository: ProjectRepository
) : ProjectFacade {
    override fun createProject(userSub: String, request: CreateProjectRequest): ProjectDto {
        val project = Project(
            userSub = userSub,
            navn = request.navn.trim(),
            beskrivelse = request.beskrivelse?.trim(),
            aktiv = true,
            opprettetDato = LocalDateTime.now(),
            endretDato = LocalDateTime.now()
        )
        val saved = projectRepository.save(project)
        return saved.toDto()
    }

    override fun getProject(userSub: String, projectId: Long): ProjectDto? {
        return projectRepository.findByIdAndUserSub(projectId, userSub)?.toDto()
    }

    override fun listProjects(userSub: String, page: Int, pageSize: Int, sort: String, asc: Boolean): ProjectListResponse {
        val projects = projectRepository.findAllByUserSub(userSub, page, pageSize, sort, asc)
        val total = projectRepository.countByUserSub(userSub)
        return ProjectListResponse(
            projects = projects.map { it.toDto() },
            page = page,
            pageSize = pageSize,
            total = total
        )
    }

    override fun updateProject(userSub: String, projectId: Long, request: UpdateProjectRequest): ProjectDto {
        val existing = projectRepository.findByIdAndUserSub(projectId, userSub)
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

    override fun deleteProject(userSub: String, projectId: Long): Boolean {
        val existing = projectRepository.findByIdAndUserSub(projectId, userSub)
            ?: throw IllegalArgumentException("Prosjekt ikke funnet")
        if (projectRepository.existsWithTimeregistrering(projectId)) {
            throw IllegalStateException("Kan ikke slette prosjekt med registrerte timer")
        }
        return projectRepository.softDelete(projectId, userSub)
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
