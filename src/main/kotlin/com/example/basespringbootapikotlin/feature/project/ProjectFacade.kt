package com.example.basespringbootapikotlin.feature.project

import com.example.basespringbootapikotlin.feature.project.*
import com.example.basespringbootapikotlin.feature.project.Project

interface ProjectFacade {
    fun createProject(userId: Long, request: CreateProjectRequest): ProjectDto
    fun getProject(userId: Long, projectId: Long): ProjectDto?
    fun listProjects(userId: Long, page: Int, pageSize: Int, sort: String, asc: Boolean): ProjectListResponse
    fun updateProject(userId: Long, projectId: Long, request: UpdateProjectRequest): ProjectDto
    fun deleteProject(userId: Long, projectId: Long): Boolean
}
