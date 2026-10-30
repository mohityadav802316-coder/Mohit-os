package com.example.domain.model

enum class ProjectType(val label: String) {
    PORTFOLIO("Portfolio"),
    WEB_APP("Web Application"),
    BACKEND_API("Backend / API"),
    E_COMMERCE("E-Commerce"),
    MOBILE_APP("Mobile Application"),
    OTHER("Custom Service")
}

enum class EnvironmentType(val label: String) {
    PRODUCTION("Production"),
    PREVIEW("Preview"),
    STAGING("Staging"),
    DEVELOPMENT("Development")
}

enum class HealthStatus(val label: String) {
    HEALTHY("Healthy"),
    DEGRADED("Degraded"),
    DOWN("Down"),
    UNCONFIGURED("Not Configured")
}

enum class HostingProvider(val label: String) {
    VERCEL("Vercel"),
    NETLIFY("Netlify"),
    CLOUD_RUN("Google Cloud Run"),
    AWS("AWS Amplify"),
    CUSTOM("Custom Server")
}

enum class DatabaseProvider(val label: String) {
    POSTGRESQL("PostgreSQL"),
    SUPABASE("Supabase"),
    FIRESTORE("Firebase Firestore"),
    NEON("Neon DB"),
    NONE("None / Static")
}

enum class RequestType(val label: String) {
    CONTENT_UPDATE("Content Update"),
    CODE_PATCH("Source Code Patch"),
    CONFIG_UPDATE("Configuration Update"),
    DEPLOYMENT("Production Deployment"),
    SCHEMA_MIGRATION("Database Schema Migration")
}

enum class RiskLevel(val label: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    CRITICAL("Critical")
}

enum class ChangeStatus(val label: String) {
    PENDING("Pending Approval"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    EXECUTED("Executed & Applied"),
    FAILED("Execution Failed")
}

enum class DeploymentStatus(val label: String) {
    READY("Ready / Live"),
    BUILDING("Building"),
    QUEUED("Queued"),
    ERROR("Build Failed"),
    CANCELED("Canceled")
}

// Exactly the 8 required states specified in the master prompt
enum class IntegrationState(val label: String) {
    NOT_CONFIGURED("Not Configured"),
    CONFIG_REQUIRED("Configuration Required"),
    READY_TO_CONNECT("Ready to Connect"),
    CONNECTED("Connected"),
    VERIFIED("Verified"),
    ACTIVE("Active"),
    DISABLED("Disabled"),
    CONNECTION_FAILED("Connection Failed")
}

enum class IntegrationProvider(val label: String) {
    GOOGLE_AUTH("Google Identity OAuth"),
    EMAIL_AUTH("Email & Pass Authentication"),
    VERCEL("Vercel Hosting"),
    GITHUB("GitHub Source"),
    POSTGRESQL("PostgreSQL / Neon"),
    SUPABASE("Supabase Cloud"),
    FIRESTORE("Firebase Firestore"),
    STORAGE("Media & Object Storage"),
    CUSTOM_REST("Custom REST API"),
    ANALYTICS("Analytics & Monitoring")
}

enum class ContentSyncStatus(val label: String) {
    DRAFT("Draft"),
    PENDING_APPROVAL("Pending Approval"),
    SAVED_TO_BACKEND("Saved to Backend"),
    LIVE_ON_WEBSITE("Live on Website")
}

data class Project(
    val id: String,
    val name: String,
    val description: String,
    val url: String,
    val icon: String,
    val projectType: ProjectType,
    val environment: EnvironmentType,
    val healthStatus: HealthStatus,
    val repositoryUrl: String,
    val hostingProvider: HostingProvider,
    val databaseProvider: DatabaseProvider,
    val lastSyncTimestamp: Long,
    val isArchived: Boolean = false,
    val pendingChangeRequestsCount: Int = 0,
    val activeDeploymentStatus: DeploymentStatus = DeploymentStatus.READY,
    val apiBaseUrl: String = "",
    val apiAuthHeader: String = "Authorization",
    val apiAuthToken: String = "",
    val apiEndpointsJson: String = "{}",
    val dbConnectionString: String = "",
    val dbSchema: String = "public",
    val vercelProjectId: String = "",
    val gitBranch: String = "main"
)

data class OwnerConfig(
    val email: String,
    val name: String,
    val isEnrolled: Boolean,
    val googleAuthEnabled: Boolean,
    val enrolledAt: Long,
    val enrollmentSecret: String = ""
)

data class DynamicContentField(
    val id: String,
    val projectId: String,
    val sectionKey: String,
    val fieldKey: String,
    val fieldLabel: String,
    val fieldType: String, // TEXT, MULTILINE, NUMBER, IMAGE_URL, BOOLEAN
    val fieldValue: String,
    val updatedAt: Long
)

data class ProjectApiTestResult(
    val isSuccess: Boolean,
    val statusCode: Int,
    val latencyMs: Int,
    val message: String,
    val headers: Map<String, String> = emptyMap()
)

data class HeroContent(
    val title: String,
    val subtitle: String,
    val badge: String,
    val bio: String,
    val avatarUrl: String,
    val ctaText: String,
    val ctaUrl: String
)

data class SkillItem(
    val id: String,
    val name: String,
    val category: String,
    val proficiency: Int, // 1-100
    val icon: String
)

data class PortfolioProjectItem(
    val id: String,
    val title: String,
    val description: String,
    val tags: List<String>,
    val liveUrl: String,
    val githubUrl: String,
    val featured: Boolean,
    val imageUrl: String
)

data class ExperienceItem(
    val id: String,
    val role: String,
    val company: String,
    val period: String,
    val description: String
)

data class ContactContent(
    val email: String,
    val github: String,
    val linkedin: String,
    val twitter: String,
    val location: String
)

data class WebsiteContentState(
    val projectId: String,
    val hero: HeroContent,
    val skills: List<SkillItem>,
    val projects: List<PortfolioProjectItem>,
    val experience: List<ExperienceItem>,
    val contact: ContactContent,
    val syncStatus: ContentSyncStatus,
    val lastSyncTime: Long
)

data class ChangeRequest(
    val id: String,
    val projectId: String,
    val projectName: String,
    val title: String,
    val reason: String,
    val requestType: RequestType,
    val riskLevel: RiskLevel,
    val creator: String,
    val targetEnvironment: EnvironmentType,
    val diffSummary: String,
    val beforeValue: String = "",
    val afterValue: String = "",
    val status: ChangeStatus,
    val createdAt: Long,
    val reviewedAt: Long? = null,
    val reviewerNotes: String? = null
)

data class Deployment(
    val id: String,
    val projectId: String,
    val projectName: String,
    val environment: EnvironmentType,
    val status: DeploymentStatus,
    val commitSha: String,
    val commitMessage: String,
    val branch: String,
    val deploymentUrl: String,
    val provider: HostingProvider,
    val triggeredBy: String,
    val timestamp: Long,
    val buildDurationSeconds: Int,
    val logs: List<String>
)

data class IntegrationSetupField(
    val key: String,
    val label: String,
    val value: String,
    val isSecret: Boolean = false,
    val placeholder: String = "",
    val description: String = ""
)

data class IntegrationService(
    val id: String,
    val name: String,
    val category: String,
    val purpose: String,
    val provider: IntegrationProvider,
    val requiredCredentials: List<String>,
    val instructions: List<String>,
    val fields: List<IntegrationSetupField>,
    val state: IntegrationState,
    val lastVerifiedTimestamp: Long? = null,
    val latencyMs: Int = 0,
    val diagnosticLog: List<String> = emptyList(),
    val errorDetails: String? = null,
    val isActive: Boolean = false
)

data class Integration(
    val id: String,
    val projectId: String,
    val provider: IntegrationProvider,
    val displayName: String,
    val endpoint: String,
    val connectionStatus: IntegrationState,
    val authMethod: String,
    val lastSync: Long,
    val latencyMs: Int,
    val errorMessage: String? = null,
    val isActive: Boolean = false
)

data class AuditLog(
    val id: String,
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

data class MediaAsset(
    val id: String,
    val projectId: String,
    val title: String,
    val url: String,
    val altText: String,
    val category: String,
    val isReachable: Boolean,
    val uploadedAt: Long
)

data class GitCommit(
    val sha: String,
    val author: String,
    val message: String,
    val date: String,
    val branch: String
)

data class GitFile(
    val path: String,
    val name: String,
    val isDirectory: Boolean,
    val size: String,
    val content: String = ""
)

data class DatabaseTableInfo(
    val name: String,
    val rowCount: Long,
    val columns: List<String>,
    val sampleRows: List<Map<String, String>>
)
