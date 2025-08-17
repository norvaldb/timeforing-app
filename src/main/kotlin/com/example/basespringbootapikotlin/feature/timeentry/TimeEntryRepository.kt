package com.example.basespringbootapikotlin.feature.timeentry

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
class TimeEntryRepository(private val jdbcTemplate: JdbcTemplate) {
    private val rowMapper = RowMapper { rs: ResultSet, _: Int ->
        TimeEntry(
            timeEntryId = rs.getLong("time_entry_id"),
            prosjektId = rs.getLong("prosjekt_id"),
            userSub = rs.getString("user_sub"),
            dato = rs.getDate("dato").toLocalDate(),
            timer = rs.getDouble("timer"),
            kommentar = rs.getString("kommentar"),
            opprettetDato = rs.getTimestamp("opprettet_dato").toLocalDateTime(),
            sistEndret = rs.getTimestamp("sist_endret").toLocalDateTime(),
            version = rs.getInt("version")
        )
    }

    fun save(entry: TimeEntry): TimeEntry {
        val keyHolder = org.springframework.jdbc.support.GeneratedKeyHolder()
        jdbcTemplate.update({ connection ->
            val ps = connection.prepareStatement(
                "INSERT INTO time_entries (prosjekt_id, user_sub, dato, timer, kommentar, opprettet_dato, sist_endret, version) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                arrayOf("time_entry_id")
            )
            ps.setLong(1, entry.prosjektId)
            ps.setString(2, entry.userSub)
            ps.setDate(3, java.sql.Date.valueOf(entry.dato))
            ps.setDouble(4, entry.timer)
            ps.setString(5, entry.kommentar)
            ps.setTimestamp(6, java.sql.Timestamp.valueOf(entry.opprettetDato))
            ps.setTimestamp(7, java.sql.Timestamp.valueOf(entry.sistEndret))
            ps.setInt(8, entry.version)
            ps
        }, keyHolder)
        val id = keyHolder.key?.toLong() ?: 0L
        return entry.copy(timeEntryId = id)
    }

    fun findByIdAndUserSub(id: Long, userSub: String): TimeEntry? =
        jdbcTemplate.query(
            "SELECT * FROM time_entries WHERE time_entry_id = ? AND user_sub = ?",
            rowMapper,
            id, userSub
        ).firstOrNull()

    fun findAllByUserSub(userSub: String, from: LocalDate?, to: LocalDate?): List<TimeEntry> {
        val sql = StringBuilder("SELECT * FROM time_entries WHERE user_sub = ?")
        val params = mutableListOf<Any>(userSub)
        if (from != null) {
            sql.append(" AND dato >= ?")
            params.add(from)
        }
        if (to != null) {
            sql.append(" AND dato <= ?")
            params.add(to)
        }
        sql.append(" ORDER BY dato DESC, time_entry_id DESC")
        return jdbcTemplate.query(sql.toString(), rowMapper, *params.toTypedArray())
    }

    fun update(entry: TimeEntry): Boolean =
        jdbcTemplate.update(
            "UPDATE time_entries SET timer = ?, kommentar = ?, sist_endret = ?, version = version + 1 WHERE time_entry_id = ? AND user_sub = ?",
            entry.timer, entry.kommentar, LocalDateTime.now(), entry.timeEntryId, entry.userSub
        ) > 0

    fun delete(id: Long, userSub: String): Boolean =
        jdbcTemplate.update(
            "DELETE FROM time_entries WHERE time_entry_id = ? AND user_sub = ?",
            id, userSub
        ) > 0
}
