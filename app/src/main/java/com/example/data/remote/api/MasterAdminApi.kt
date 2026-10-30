package com.example.data.remote.api

interface MasterAdminApi {
    suspend fun getHealth(): Result<ApiHealthResponse>

    suspend fun getProjects(): Result<List<ApiProjectDto>>

    suspend fun createProject(project: ApiProjectDto): Result<ApiProjectDto>

    suspend fun updateHeroContent(
        projectId: String,
        hero: ApiHeroContentDto
    ): Result<ApiContentUpdateResponse>

    suspend fun triggerDeployment(
        request: ApiDeploymentTriggerRequest
    ): Result<ApiDeploymentResponse>
}
