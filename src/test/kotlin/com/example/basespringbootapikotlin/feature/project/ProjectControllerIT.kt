package com.example.basespringbootapikotlin.feature.project

import com.example.basespringbootapikotlin.feature.project.CreateProjectRequest
import com.example.basespringbootapikotlin.feature.project.UpdateProjectRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import com.example.basespringbootapikotlin.config.TestSecurityConfig
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.jdbc.core.JdbcTemplate

import com.example.basespringbootapikotlin.config.OracleTestContainerConfig

@SpringBootTest(classes = [TestSecurityConfig::class])
@AutoConfigureMockMvc

class ProjectControllerIT @Autowired constructor(
	mockMvc: MockMvc,
	objectMapper: ObjectMapper,
	@Autowired private val jdbcTemplate: JdbcTemplate
) : OracleTestContainerConfig() {
	val mockMvc: MockMvc = mockMvc
	val objectMapper: ObjectMapper = objectMapper
	private val baseUrl = "/api/projects"

	@BeforeEach
	fun setup() {
		jdbcTemplate.execute("TRUNCATE TABLE projects")
	}

	@Test
	fun `create and fetch project`() {
		val request = CreateProjectRequest(navn = "Testprosjekt", beskrivelse = "Test")
		val json = objectMapper.writeValueAsString(request)
		// Simulate JWT with sub claim (stateless)
		val mvcResult = mockMvc.perform(post(baseUrl)
			.contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", "Bearer dummy-jwt-with-sub-claim")
			.content(json))
			.andExpect(status().isCreated)
			.andExpect(jsonPath("$.navn").value("Testprosjekt"))
			.andReturn()
		val response = mvcResult.response.contentAsString
		val createdId = objectMapper.readTree(response).get("projectId").asLong()
		// Fetch
		mockMvc.perform(get("$baseUrl/$createdId")
			.header("Authorization", "Bearer dummy-jwt-with-sub-claim"))
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.navn").value("Testprosjekt"))
	}

	@Test
	fun `update project`() {
		// Create
		val create = CreateProjectRequest(navn = "P1", beskrivelse = "B1")
		val createJson = objectMapper.writeValueAsString(create)
		val mvcResult = mockMvc.perform(post(baseUrl)
			.contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", "Bearer dummy-jwt-with-sub-claim")
			.content(createJson))
			.andExpect(status().isCreated)
			.andReturn()
		val id = objectMapper.readTree(mvcResult.response.contentAsString).get("projectId").asLong()
		// Update
		val update = UpdateProjectRequest(navn = "P2", beskrivelse = "B2")
		val updateJson = objectMapper.writeValueAsString(update)
		mockMvc.perform(put("$baseUrl/$id")
			.contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", "Bearer dummy-jwt-with-sub-claim")
			.content(updateJson))
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.navn").value("P2"))
	}

	@Test
	fun `delete project`() {
		// Create
		val create = CreateProjectRequest(navn = "P1", beskrivelse = "B1")
		val createJson = objectMapper.writeValueAsString(create)
		val mvcResult = mockMvc.perform(post(baseUrl)
			.contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", "Bearer dummy-jwt-with-sub-claim")
			.content(createJson))
			.andExpect(status().isCreated)
			.andReturn()
		val id = objectMapper.readTree(mvcResult.response.contentAsString).get("projectId").asLong()
		// Delete
		mockMvc.perform(delete("$baseUrl/$id")
			.header("Authorization", "Bearer dummy-jwt-with-sub-claim"))
			.andExpect(status().isNoContent)
		// Should not be found
		mockMvc.perform(get("$baseUrl/$id")
			.header("Authorization", "Bearer dummy-jwt-with-sub-claim"))
			.andExpect(status().isNotFound)
	}

	@Test
	fun `list projects paginated`() {
		// Create two projects
		val req1 = CreateProjectRequest(navn = "P1", beskrivelse = "B1")
		val req2 = CreateProjectRequest(navn = "P2", beskrivelse = "B2")
		mockMvc.perform(post(baseUrl)
			.contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", "Bearer dummy-jwt-with-sub-claim")
			.content(objectMapper.writeValueAsString(req1)))
			.andExpect(status().isCreated)
		mockMvc.perform(post(baseUrl)
			.contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", "Bearer dummy-jwt-with-sub-claim")
			.content(objectMapper.writeValueAsString(req2)))
			.andExpect(status().isCreated)
		// List
		mockMvc.perform(get(baseUrl)
			.param("page", "1").param("pageSize", "10")
			.header("Authorization", "Bearer dummy-jwt-with-sub-claim"))
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.projects").isArray)
			.andExpect(jsonPath("$.projects.length()").value(2))
	}
}
// ...existing code...
