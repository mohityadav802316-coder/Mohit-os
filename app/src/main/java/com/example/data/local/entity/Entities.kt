package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
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
    val lastSyncTimestamp: Long,
    val isArchived: Boolean,
    val pendingChangeRequestsCount: Int,
    val activeDeploymentStatus: String,
    val apiBaseUrl: String = "",
    val apiAuthHeader: String = "Authorization",
    val apiAuthToken: String = "",
    val apiEndpointsJson: String = "{}",
    val dbConnectionString: String = "",
    val dbSchema: String = "public",
    val vercelProjectId: String = "",
    val gitBranch: String = "main"
)

@Entity(tableName = "owner_config")
data class OwnerConfigEntity(
    @PrimaryKey val id: String = "primary_owner",
    val email: String,
    val name: String,
    val isEnrolled: Boolean,
    val passwordHash: String,
    val googleAuthEnabled: Boolean,
    val enrolledAt: Long,
    val securityToken: String
)

@Entity(tableName = "dynamic_content_fields")
data class DynamicContentFieldEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val sectionKey: String,
    val fieldKey: String,
    val fieldLabel: String,
    val fieldType: String,
    val fieldValue: String,
    val updatedAt: Long
)

@Entity(tableName = "change_requests")
data class ChangeRequestEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val projectName: String,
    val title: String,
    val reason: String,
    val requestType: String,
    val riskLevel: String,
    val creator: String,
    val targetEnvironment: String,
    val diffSummary: String,
    val beforeValue: String,
    val afterValue: String,
    val status: String,
    val createdAt: Long,
    val reviewedAt: Long?,
    val reviewerNotes: String?
)

@Entity(tableName = "deployments")
data class DeploymentEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val projectName: String,
    val environment: String,
    val status: String,
    val commitSha: String,
    val commitMessage: String,
    val branch: String,
    val deploymentUrl: String,
    val provider: String,
    val triggeredBy: String,
    val timestamp: Long,
    val buildDurationSeconds: Int,
    val logsJson: String
)

@Entity(tableName = "integrations")
data class IntegrationEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val provider: String,
    val displayName: String,
    val category: String,
    val purpose: String,
    val endpoint: String,
    val connectionStatus: String,
    val authMethod: String,
    val fieldsJson: String,
    val diagnosticLogJson: String,
    val lastSync: Long,
    val latencyMs: Int,
    val errorMessage: String?,
    val isActive: Boolean
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey val id: String,
    val actor: String,
    val action: String,
    val resourceType: String,
    val resourceId: String,
    val projectId: String,
    val timestamp: Long,
    val status: String,
    val correlationId: String,
    val details: String
)

@Entity(tableName = "media_assets")
data class MediaAssetEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val title: String,
    val url: String,
    val altText: String,
    val category: String,
    val isReachable: Boolean,
    val uploadedAt: Long
)
