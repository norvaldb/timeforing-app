package com.example.basespringbootapikotlin.repository

import com.example.basespringbootapikotlin.model.User
import com.example.basespringbootapikotlin.model.UserStatus
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDateTime

@Repository
class UserRepository(private val jdbcTemplate: JdbcTemplate) {
    
    private val userRowMapper = RowMapper { rs: ResultSet, _: Int ->
        User(
            userId = rs.getLong("user_id"),
            navn = rs.getString("navn"),
            mobil = rs.getString("mobil"),
            epost = rs.getString("epost"),
            status = UserStatus.valueOf(rs.getString("status")),
            opprettetDato = rs.getTimestamp("opprettet_dato").toLocalDateTime(),
            sistEndret = rs.getTimestamp("sist_endret").toLocalDateTime(),
            version = rs.getLong("version")
        )
    }
    
    fun findById(id: Long): User? = try {
        jdbcTemplate.queryForObject(
            """
            SELECT user_id, navn, mobil, epost, status, opprettet_dato, sist_endret, version
            FROM users 
            WHERE user_id = ? AND deleted = 0
            """,
            userRowMapper,
            id
        )
    } catch (e: EmptyResultDataAccessException) {
        null
    }
    
    fun findByEpost(epost: String): User? = try {
        jdbcTemplate.queryForObject(
            """
            SELECT user_id, navn, mobil, epost, status, opprettet_dato, sist_endret, version
            FROM users 
            WHERE epost = ? AND deleted = 0
            """,
            userRowMapper,
            epost.lowercase()
        )
    } catch (e: EmptyResultDataAccessException) {
        null
    }
    
    fun existsByEpost(epost: String): Boolean {
        val count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM users WHERE epost = ? AND deleted = 0",
            Long::class.java,
            epost.lowercase()
        )
        return count != null && count > 0
    }
    
    fun save(user: User): User {
        val now = LocalDateTime.now()
        
        if (user.userId == 0L) {
            // Insert new user using Oracle sequence
            val sql = """
                INSERT INTO users (user_id, navn, mobil, epost, status, opprettet_dato, sist_endret, version)
                VALUES (users_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)
            """
            
            jdbcTemplate.update(
                sql,
                user.navn,
                normalizeMobileNumber(user.mobil),
                user.epost.lowercase(),
                user.status.name,
                now,
                now,
                1L
            )
            
            // Get the generated ID from the sequence
            val id = jdbcTemplate.queryForObject(
                "SELECT users_seq.CURRVAL FROM dual",
                Long::class.java
            )
            return user.copy(
                userId = id ?: 0,
                epost = user.epost.lowercase(),
                mobil = normalizeMobileNumber(user.mobil),
                opprettetDato = now,
                sistEndret = now,
                version = 1L
            )
        } else {
            // Update existing user
            val rowsUpdated = jdbcTemplate.update(
                """
                UPDATE users 
                SET navn = ?, mobil = ?, epost = ?, status = ?, sist_endret = ?, version = version + 1
                WHERE user_id = ? AND version = ?
                """,
                user.navn,
                normalizeMobileNumber(user.mobil),
                user.epost.lowercase(),
                user.status.name,
                now,
                user.userId,
                user.version
            )
            
            if (rowsUpdated == 0) {
                throw IllegalStateException("User was modified by another transaction or does not exist")
            }
            
            return user.copy(
                epost = user.epost.lowercase(),
                mobil = normalizeMobileNumber(user.mobil),
                sistEndret = now,
                version = user.version + 1
            )
        }
    }
    
    fun delete(id: Long): Boolean {
    val rowsUpdated = jdbcTemplate.update("UPDATE users SET deleted = 1 WHERE user_id = ? AND deleted = 0", id)
    return rowsUpdated > 0
    }
    
    fun findAll(limit: Int = 100, offset: Int = 0): List<User> {
        return jdbcTemplate.query(
            """
            SELECT user_id, navn, mobil, epost, status, opprettet_dato, sist_endret, version
            FROM users 
            WHERE deleted = 0
            ORDER BY opprettet_dato DESC
            LIMIT ? OFFSET ?
            """,
            userRowMapper,
            limit,
            offset
        )
    }
    
    /**
     * Normalize mobile number to consistent format (+47xxxxxxxx)
     */
    private fun normalizeMobileNumber(mobil: String): String {
        val cleaned = mobil.replace("\\s".toRegex(), "")
        return when {
            cleaned.startsWith("+47") -> cleaned
            cleaned.startsWith("47") -> "+$cleaned"
            cleaned.startsWith("0047") -> "+${cleaned.substring(2)}"
            cleaned.length == 8 -> "+47$cleaned"
            else -> mobil // Return as-is if format is unexpected
        }
    }
}
