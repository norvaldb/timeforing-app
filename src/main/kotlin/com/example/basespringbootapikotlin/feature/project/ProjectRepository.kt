package com.example.basespringbootapikotlin.feature.project

import com.example.basespringbootapikotlin.feature.project.Project
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDateTime

import org.slf4j.LoggerFactory
import org.slf4j.MDC

@Repository
class ProjectRepository(private val jdbcTemplate: JdbcTemplate) {
    private val log = LoggerFactory.getLogger(ProjectRepository::class.java)
    private val rowMapper = RowMapper { rs: ResultSet, _: Int ->
        Project(
            projectId = rs.getLong("project_id"),
            userSub = rs.getString("user_sub"),
            navn = rs.getString("navn"),
            beskrivelse = rs.getString("beskrivelse"),
            aktiv = rs.getInt("aktiv") == 1,
            opprettetDato = rs.getTimestamp("opprettet_dato").toLocalDateTime(),
            endretDato = rs.getTimestamp("endret_dato").toLocalDateTime()
        )
    }

    fun save(project: Project): Project {
    val correlationId = MDC.get("correlationId")
    val userSub = project.userSub
    log.info("Saving project for userSub={} [correlationId={}]", userSub, correlationId)
        val keyHolder = org.springframework.jdbc.support.GeneratedKeyHolder()
        jdbcTemplate.update({ connection ->
            val ps = connection.prepareStatement(
                "INSERT INTO projects (user_sub, navn, beskrivelse, aktiv, opprettet_dato, endret_dato) VALUES (?, ?, ?, ?, ?, ?)",
                arrayOf("project_id")
            )
            ps.setString(1, project.userSub)
            ps.setString(2, project.navn)
            ps.setString(3, project.beskrivelse)
            ps.setInt(4, if (project.aktiv) 1 else 0)
            ps.setTimestamp(5, java.sql.Timestamp.valueOf(project.opprettetDato))
            ps.setTimestamp(6, java.sql.Timestamp.valueOf(project.endretDato))
            ps
        }, keyHolder)
        val id = keyHolder.key?.toLong() ?: 0L
    log.info("Project saved with id={} for userSub={} [correlationId={}]", id, userSub, correlationId)
        return project.copy(projectId = id)
    }

    fun findByIdAndUserSub(projectId: Long, userSub: String): Project? =
        jdbcTemplate.query(
            "SELECT * FROM projects WHERE project_id = ? AND user_sub = ? AND aktiv = 1",
            rowMapper,
            projectId, userSub
        ).firstOrNull()

    fun findAllByUserSub(userSub: String, page: Int, pageSize: Int, sort: String = "navn", asc: Boolean = true): List<Project> =
        jdbcTemplate.query(
            "SELECT * FROM projects WHERE user_sub = ? AND aktiv = 1 ORDER BY $sort ${if (asc) "ASC" else "DESC"} OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
            rowMapper,
            userSub, (page - 1) * pageSize, pageSize
        )

    fun countByUserSub(userSub: String): Long =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM projects WHERE user_sub = ? AND aktiv = 1",
            Long::class.java,
            userSub
        ) ?: 0

    fun update(project: Project): Boolean =
        jdbcTemplate.update(
            "UPDATE projects SET navn = ?, beskrivelse = ?, endret_dato = ? WHERE project_id = ? AND user_sub = ?",
            project.navn, project.beskrivelse, project.endretDato, project.projectId, project.userSub
        ).also { updated ->
            val correlationId = MDC.get("correlationId")
            log.info("Updated project id={} for userSub={} [correlationId={}]. Success={}", project.projectId, project.userSub, correlationId, updated > 0)
        } > 0

    fun softDelete(projectId: Long, userSub: String): Boolean =
        jdbcTemplate.update(
            "UPDATE projects SET aktiv = 0, endret_dato = ? WHERE project_id = ? AND user_sub = ?",
            LocalDateTime.now(), projectId, userSub
        ).also { deleted ->
            val correlationId = MDC.get("correlationId")
            log.info("Soft deleted project id={} for userSub={} [correlationId={}]. Success={}", projectId, userSub, correlationId, deleted > 0)
        } > 0

    fun existsWithTimeregistrering(projectId: Long): Boolean =
        (jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM time_entries WHERE prosjekt_id = ?",
            Long::class.java,
            projectId
        ) ?: 0L) > 0
}
