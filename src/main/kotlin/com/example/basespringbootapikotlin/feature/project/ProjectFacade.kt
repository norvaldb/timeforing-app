package com.example.basespringbootapikotlin.feature.project

import com.example.basespringbootapikotlin.feature.project.*
import com.example.basespringbootapikotlin.feature.project.Project

interface ProjectFacade {
    fun createProject(userSub: String, request: CreateProjectRequest): ProjectDto
    fun getProject(userSub: String, projectId: Long): ProjectDto?
    fun listProjects(userSub: String, page: Int, pageSize: Int, sort: String, asc: Boolean): ProjectListResponse
    fun updateProject(userSub: String, projectId: Long, request: UpdateProjectRequest): ProjectDto
    fun deleteProject(userSub: String, projectId: Long): Boolean
}
