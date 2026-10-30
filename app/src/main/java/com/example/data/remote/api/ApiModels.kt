package com.example.data.remote.api

data class ApiHealthResponse(
    val status: String,
    val version: String,
    val timestamp: Long,
    val environment: String
)

data class ApiProjectDto(
    val id: String,
    val name: String,
    val description: String,
    val url: String,
    val icon: String,
    val projectType: String,
    val environment: String,
    val healthStatus: String,
    val repositoryUrl: String,
    val hostingProvider: String,
    val databaseProvider: String,
    val lastSyncTimestamp: Long
)

data class ApiHeroContentDto(
    val title: String,
    val subtitle: String,
    val badge: String,
    val bio: String,
    val avatarUrl: String,
    val ctaText: String,
    val ctaUrl: String
)

data class ApiContentUpdateResponse(
    val success: Boolean,
    val message: String,
    val timestamp: Long,
    val syncStatus: String
)

data class ApiDeploymentTriggerRequest(
    val projectId: String,
    val targetEnvironment: String,
    val commitSha: String,
    val approvedBy: String
)

data class ApiDeploymentResponse(
    val deploymentId: String,
    val status: String,
    val url: String,
    val timestamp: Long
)
