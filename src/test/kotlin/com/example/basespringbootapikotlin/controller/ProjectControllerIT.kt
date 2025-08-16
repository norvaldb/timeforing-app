package com.example.basespringbootapikotlin.controller

import com.example.basespringbootapikotlin.dto.CreateProjectRequest
import com.example.basespringbootapikotlin.dto.UpdateProjectRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerIT @Autowired constructor(
	mockMvc: MockMvc,
	objectMapper: ObjectMapper
) : OracleTestContainerConfig() {
	val mockMvc: MockMvc = mockMvc
	val objectMapper: ObjectMapper = objectMapper
	private val baseUrl = "/api/projects"

	@Autowired
	lateinit var jdbcTemplate: org.springframework.jdbc.core.JdbcTemplate

	@BeforeEach
	fun setup() {
		// Clean up tables for test isolation
		jdbcTemplate.execute("TRUNCATE TABLE projects")
		jdbcTemplate.execute("TRUNCATE TABLE users")
		// Re-insert test user with user_id = 1
		jdbcTemplate.execute("""
			INSERT INTO users (user_id, navn, mobil, epost, status, opprettet_dato, sist_endret, version)
			VALUES (1, 'Test User', '+4712345678', 'testuser@example.com', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1)
		""")
	}

	@Test
	@WithMockUser(username = "1", roles = ["USER"])
	fun `create and fetch project`() {
		val request = CreateProjectRequest(navn = "Testprosjekt", beskrivelse = "Test")
		val json = objectMapper.writeValueAsString(request)
		val mvcResult = mockMvc.perform(post(baseUrl)
			.contentType(MediaType.APPLICATION_JSON)
			.content(json))
			.andExpect(status().isCreated)
			.andExpect(jsonPath("$.navn").value("Testprosjekt"))
			.andReturn()
		val response = mvcResult.response.contentAsString
		val createdId = objectMapper.readTree(response).get("projectId").asLong()
		// Fetch
		mockMvc.perform(get("$baseUrl/$createdId"))
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.navn").value("Testprosjekt"))
	}

	@Test
	@WithMockUser(username = "1", roles = ["USER"])
	fun `update project`() {
		// Create
		val create = CreateProjectRequest(navn = "P1", beskrivelse = "B1")
		val createJson = objectMapper.writeValueAsString(create)
		val mvcResult = mockMvc.perform(post(baseUrl)
			.contentType(MediaType.APPLICATION_JSON)
			.content(createJson))
			.andExpect(status().isCreated)
			.andReturn()
		val id = objectMapper.readTree(mvcResult.response.contentAsString).get("projectId").asLong()
		// Update
		val update = UpdateProjectRequest(navn = "P2", beskrivelse = "B2")
		val updateJson = objectMapper.writeValueAsString(update)
		mockMvc.perform(put("$baseUrl/$id")
			.contentType(MediaType.APPLICATION_JSON)
			.content(updateJson))
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.navn").value("P2"))
	}

	@Test
	@WithMockUser(username = "1", roles = ["USER"])
	fun `delete project`() {
		// Create
		val create = CreateProjectRequest(navn = "P1", beskrivelse = "B1")
		val createJson = objectMapper.writeValueAsString(create)
		val mvcResult = mockMvc.perform(post(baseUrl)
			.contentType(MediaType.APPLICATION_JSON)
			.content(createJson))
			.andExpect(status().isCreated)
			.andReturn()
		val id = objectMapper.readTree(mvcResult.response.contentAsString).get("projectId").asLong()
		// Delete
		mockMvc.perform(delete("$baseUrl/$id"))
			.andExpect(status().isNoContent)
		// Should not be found
		mockMvc.perform(get("$baseUrl/$id"))
			.andExpect(status().isNotFound)
	}

	@Test
	@WithMockUser(username = "1", roles = ["USER"])
	fun `list projects paginated`() {
		// Create two projects
		val req1 = CreateProjectRequest(navn = "P1", beskrivelse = "B1")
		val req2 = CreateProjectRequest(navn = "P2", beskrivelse = "B2")
		mockMvc.perform(post(baseUrl)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(req1)))
			.andExpect(status().isCreated)
		mockMvc.perform(post(baseUrl)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(req2)))
			.andExpect(status().isCreated)
		// List
		mockMvc.perform(get(baseUrl).param("page", "1").param("pageSize", "10"))
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.projects").isArray)
			.andExpect(jsonPath("$.projects.length()").value(2))
	}
}
// ...existing code...
