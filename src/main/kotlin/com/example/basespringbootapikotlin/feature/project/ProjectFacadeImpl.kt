package com.example.basespringbootapikotlin.feature.project

import com.example.basespringbootapikotlin.feature.project.*
import com.example.basespringbootapikotlin.feature.project.Project
import com.example.basespringbootapikotlin.feature.project.ProjectRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

import org.slf4j.LoggerFactory
import org.slf4j.MDC

@Service
@Transactional
class ProjectFacadeImpl(
    private val projectRepository: ProjectRepository
) : ProjectFacade {
    private val log = LoggerFactory.getLogger(ProjectFacadeImpl::class.java)
    override fun createProject(userSub: String, request: CreateProjectRequest): ProjectDto {
    val correlationId = MDC.get("correlationId")
    log.info("Creating project for userSub={} [correlationId={}]", userSub, correlationId)
        val project = Project(
            userSub = userSub,
            navn = request.navn.trim(),
            beskrivelse = request.beskrivelse?.trim(),
            aktiv = true,
            opprettetDato = LocalDateTime.now(),
            endretDato = LocalDateTime.now()
        )
        val saved = projectRepository.save(project)
    log.info("Project created with id={} for userSub={} [correlationId={}]", saved.projectId, userSub, correlationId)
        return saved.toDto()
    }

    override fun getProject(userSub: String, projectId: Long): ProjectDto? {
    val correlationId = MDC.get("correlationId")
    log.info("Getting project id={} for userSub={} [correlationId={}]", projectId, userSub, correlationId)
        return projectRepository.findByIdAndUserSub(projectId, userSub)?.toDto()
    }

    override fun listProjects(userSub: String, page: Int, pageSize: Int, sort: String, asc: Boolean): ProjectListResponse {
    val correlationId = MDC.get("correlationId")
    log.info("Listing projects for userSub={} [correlationId={}] page={} pageSize={}", userSub, correlationId, page, pageSize)
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
        val correlationId = MDC.get("correlationId")
        log.info("Updating project id={} for userSub={} [correlationId={}]", projectId, userSub, correlationId)
        val existing = projectRepository.findByIdAndUserSub(projectId, userSub)
            ?: throw IllegalArgumentException("Prosjekt ikke funnet")
        val updated = existing.copy(
            navn = request.navn.trim(),
            beskrivelse = request.beskrivelse?.trim(),
            endretDato = LocalDateTime.now()
        )
        if (!projectRepository.update(updated)) {
            log.error("Failed to update project id={} for userSub={} [correlationId={}]", projectId, userSub, correlationId)
            throw IllegalStateException("Kunne ikke oppdatere prosjekt")
        }
        log.info("Project updated id={} for userSub={} [correlationId={}]", projectId, userSub, correlationId)
        return updated.toDto()
    }

    override fun deleteProject(userSub: String, projectId: Long): Boolean {
        val correlationId = MDC.get("correlationId")
        log.info("Deleting project id={} for userSub={} [correlationId={}]", projectId, userSub, correlationId)
        val existing = projectRepository.findByIdAndUserSub(projectId, userSub)
            ?: throw IllegalArgumentException("Prosjekt ikke funnet")
        if (projectRepository.existsWithTimeregistrering(projectId)) {
            log.error("Cannot delete project id={} with time entries for userSub={} [correlationId={}]", projectId, userSub, correlationId)
            throw IllegalStateException("Kan ikke slette prosjekt med registrerte timer")
        }
        log.info("Project deleted id={} for userSub={} [correlationId={}]", projectId, userSub, correlationId)
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
