package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.MasterAdminRepository
import com.example.domain.model.AuditLog
import com.example.domain.model.ChangeRequest
import com.example.domain.model.DatabaseProvider
import com.example.domain.model.Deployment
import com.example.domain.model.DynamicContentField
import com.example.domain.model.EnvironmentType
import com.example.domain.model.HeroContent
import com.example.domain.model.HostingProvider
import com.example.domain.model.Integration
import com.example.domain.model.MediaAsset
import com.example.domain.model.OwnerConfig
import com.example.domain.model.PortfolioProjectItem
import com.example.domain.model.Project
import com.example.domain.model.ProjectApiTestResult
import com.example.domain.model.ProjectType
import com.example.domain.model.RequestType
import com.example.domain.model.RiskLevel
import com.example.domain.model.WebsiteContentState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
    data class ShowError(val error: String) : UiEvent()
}

class MasterAdminViewModel(
    private val repository: MasterAdminRepository
) : ViewModel() {

    // UI Feedback Event Stream
    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents: SharedFlow<UiEvent> = _uiEvents.asSharedFlow()

    // Navigation and Active View State
    private val _selectedProjectId = MutableStateFlow<String?>("proj_mohitport")
    val selectedProjectId: StateFlow<String?> = _selectedProjectId.asStateFlow()

    private val _activeProjectTab = MutableStateFlow(0) // 0: Overview, 1: CMS, 2: Code, 3: Deployments, 4: Database, 5: Media
    val activeProjectTab: StateFlow<Int> = _activeProjectTab.asStateFlow()

    // Search and filter state
    val searchQuery = MutableStateFlow("")
    val projectTypeFilter = MutableStateFlow<ProjectType?>(null)

    // Repository Flows
    val rawProjects: StateFlow<List<Project>> = repository.projects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredProjects: StateFlow<List<Project>> = combine(
        rawProjects,
        searchQuery,
        projectTypeFilter
    ) { list, query, typeFilter ->
        list.filter { project ->
            val matchesQuery = query.isBlank() ||
                    project.name.contains(query, ignoreCase = true) ||
                    project.description.contains(query, ignoreCase = true) ||
                    project.url.contains(query, ignoreCase = true)
            val matchesType = typeFilter == null || project.projectType == typeFilter
            matchesQuery && matchesType
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val changeRequests: StateFlow<List<ChangeRequest>> = repository.changeRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deployments: StateFlow<List<Deployment>> = repository.deployments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val integrations: StateFlow<List<Integration>> = repository.integrations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mediaAssets: StateFlow<List<MediaAsset>> = repository.mediaAssets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val auditLogs: StateFlow<List<AuditLog>> = repository.auditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val websiteContent: StateFlow<WebsiteContentState> = repository.contentState

    // Master Setup & Integration Center Flow
    val integrationServices: StateFlow<List<com.example.domain.model.IntegrationService>> = repository.integrationServices
    val selectedSetupServiceId = MutableStateFlow<String?>(null)

    val ownerConfig: StateFlow<OwnerConfig> = repository.ownerConfig
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            OwnerConfig(
                email = "mohityadav802316@gmail.com",
                name = "Mohit Yadav",
                isEnrolled = false,
                googleAuthEnabled = true,
                enrolledAt = 0L,
                enrollmentSecret = "MASTER_BOOTSTRAP_2026"
            )
        )

    val ownerName = repository.ownerName
    val ownerEmail = repository.ownerEmail
    val ownerRole = repository.ownerRole

    fun enrollOwner(
        name: String,
        email: String,
        password: String,
        bootstrapCode: String,
        enableGoogle: Boolean,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.enrollOwner(name, email, password, bootstrapCode, enableGoogle)
            result.onSuccess { config ->
                _uiEvents.emit(UiEvent.ShowToast("Owner account established for ${config.email}! Enrollment locked."))
                onSuccess()
            }.onFailure { err ->
                _uiEvents.emit(UiEvent.ShowError(err.message ?: "Owner enrollment failed"))
            }
        }
    }

    fun resetOwnerEnrollment(password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = repository.resetOwnerEnrollment(password)
            result.onSuccess {
                _uiEvents.emit(UiEvent.ShowToast("Owner enrollment reset successfully."))
                onSuccess()
            }.onFailure { err ->
                _uiEvents.emit(UiEvent.ShowError(err.message ?: "Password verification failed"))
            }
        }
    }

    fun selectProject(id: String?) {
        _selectedProjectId.value = id
        _activeProjectTab.value = 0
    }

    fun setProjectTab(tabIndex: Int) {
        _activeProjectTab.value = tabIndex
    }

    fun addProject(
        name: String,
        description: String,
        url: String,
        type: ProjectType,
        environment: EnvironmentType,
        repo: String,
        hosting: HostingProvider,
        db: DatabaseProvider,
        apiBaseUrl: String = "",
        apiAuthHeader: String = "Authorization",
        apiAuthToken: String = "",
        dbConnectionString: String = "",
        vercelProjectId: String = "",
        gitBranch: String = "main"
    ) {
        viewModelScope.launch {
            try {
                val newProject = repository.addProject(
                    name = name,
                    description = description,
                    url = url,
                    projectType = type,
                    environment = environment,
                    repositoryUrl = repo,
                    hostingProvider = hosting,
                    databaseProvider = db,
                    apiBaseUrl = apiBaseUrl,
                    apiAuthHeader = apiAuthHeader,
                    apiAuthToken = apiAuthToken,
                    dbConnectionString = dbConnectionString,
                    vercelProjectId = vercelProjectId,
                    gitBranch = gitBranch
                )
                _uiEvents.emit(UiEvent.ShowToast("Registered '${newProject.name}' successfully"))
            } catch (e: Exception) {
                _uiEvents.emit(UiEvent.ShowError("Failed to add project: ${e.localizedMessage}"))
            }
        }
    }

    fun updateProjectConfiguration(
        projectId: String,
        apiBaseUrl: String,
        apiAuthHeader: String,
        apiAuthToken: String,
        dbConnectionString: String,
        vercelProjectId: String,
        gitBranch: String
    ) {
        viewModelScope.launch {
            try {
                repository.updateProjectConfiguration(
                    projectId = projectId,
                    apiBaseUrl = apiBaseUrl,
                    apiAuthHeader = apiAuthHeader,
                    apiAuthToken = apiAuthToken,
                    dbConnectionString = dbConnectionString,
                    vercelProjectId = vercelProjectId,
                    gitBranch = gitBranch
                )
                _uiEvents.emit(UiEvent.ShowToast("Project API & Database configurations saved successfully!"))
            } catch (e: Exception) {
                _uiEvents.emit(UiEvent.ShowError("Save configuration error: ${e.localizedMessage}"))
            }
        }
    }

    fun testProjectApiConnection(
        projectId: String,
        url: String,
        authHeader: String,
        token: String,
        onResult: (ProjectApiTestResult) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.testProjectApiConnection(projectId, url, authHeader, token)
            result.onSuccess { testResult ->
                onResult(testResult)
                _uiEvents.emit(UiEvent.ShowToast(testResult.message))
            }.onFailure { err ->
                _uiEvents.emit(UiEvent.ShowError(err.message ?: "API test connection failed"))
            }
        }
    }

    fun testProjectDatabaseConnection(
        projectId: String,
        provider: DatabaseProvider,
        connectionUri: String,
        onResult: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.testProjectDatabaseConnection(projectId, provider, connectionUri)
            result.onSuccess { msg ->
                onResult(msg)
                _uiEvents.emit(UiEvent.ShowToast(msg))
            }.onFailure { err ->
                _uiEvents.emit(UiEvent.ShowError(err.message ?: "Database connection test failed"))
            }
        }
    }

    // Dynamic CMS Fields
    fun getDynamicFieldsForProject(projectId: String) = repository.getDynamicFieldsForProject(projectId)

    fun addDynamicField(
        projectId: String,
        sectionKey: String,
        fieldKey: String,
        fieldLabel: String,
        fieldType: String,
        defaultValue: String
    ) {
        viewModelScope.launch {
            try {
                repository.addDynamicField(projectId, sectionKey, fieldKey, fieldLabel, fieldType, defaultValue)
                _uiEvents.emit(UiEvent.ShowToast("Custom field '$fieldLabel' added to CMS schema!"))
            } catch (e: Exception) {
                _uiEvents.emit(UiEvent.ShowError("Failed to add field: ${e.localizedMessage}"))
            }
        }
    }

    fun updateDynamicFieldValue(fieldId: String, newValue: String) {
        viewModelScope.launch {
            repository.updateDynamicFieldValue(fieldId, newValue)
        }
    }

    fun deleteDynamicField(fieldId: String) {
        viewModelScope.launch {
            repository.deleteDynamicField(fieldId)
            _uiEvents.emit(UiEvent.ShowToast("Field removed from CMS schema."))
        }
    }

    fun archiveProject(id: String) {
        viewModelScope.launch {
            try {
                repository.archiveProject(id)
                _uiEvents.emit(UiEvent.ShowToast("Project archived (remote resources intact)"))
            } catch (e: Exception) {
                _uiEvents.emit(UiEvent.ShowError("Archive failed: ${e.localizedMessage}"))
            }
        }
    }

    // CMS Operations
    fun updateHeroContent(hero: HeroContent) {
        viewModelScope.launch {
            try {
                repository.updateHeroContent(hero)
                _uiEvents.emit(UiEvent.ShowToast("Hero content updated and saved to backend!"))
            } catch (e: Exception) {
                _uiEvents.emit(UiEvent.ShowError("Failed to update hero: ${e.localizedMessage}"))
            }
        }
    }

    fun addPortfolioProjectItem(title: String, desc: String, tags: List<String>, liveUrl: String, ghUrl: String, imgUrl: String) {
        viewModelScope.launch {
            val item = PortfolioProjectItem(
                id = "proj_item_" + System.currentTimeMillis().toString().takeLast(6),
                title = title,
                description = desc,
                tags = tags,
                liveUrl = liveUrl,
                githubUrl = ghUrl,
                featured = true,
                imageUrl = imgUrl.ifBlank { "https://images.unsplash.com/photo-1550751827-4bd374c3f58b?auto=format&fit=crop&w=800&q=80" }
            )
            repository.addPortfolioProjectItem(item)
            _uiEvents.emit(UiEvent.ShowToast("Project '$title' added to portfolio showcase"))
        }
    }

    fun updateSkillProficiency(skillId: String, newProficiency: Int) {
        viewModelScope.launch {
            repository.updateSkillProficiency(skillId, newProficiency)
        }
    }

    fun publishWebsiteContent(projectId: String) {
        viewModelScope.launch {
            try {
                val result = repository.publishWebsiteChanges(projectId)
                result.onSuccess { msg ->
                    _uiEvents.emit(UiEvent.ShowToast(msg))
                }
            } catch (e: Exception) {
                _uiEvents.emit(UiEvent.ShowError("Publish error: ${e.localizedMessage}"))
            }
        }
    }

    // Change Requests
    fun submitChangeRequest(
        projectId: String,
        projectName: String,
        title: String,
        reason: String,
        type: RequestType,
        risk: RiskLevel,
        env: EnvironmentType,
        diff: String,
        before: String,
        after: String
    ) {
        viewModelScope.launch {
            repository.submitChangeRequest(
                projectId = projectId,
                projectName = projectName,
                title = title,
                reason = reason,
                requestType = type,
                riskLevel = risk,
                targetEnvironment = env,
                diffSummary = diff,
                beforeValue = before,
                afterValue = after
            )
            _uiEvents.emit(UiEvent.ShowToast("Change request submitted for owner approval"))
        }
    }

    fun approveChangeRequest(id: String, notes: String?) {
        viewModelScope.launch {
            repository.approveAndExecuteChangeRequest(id, notes)
            _uiEvents.emit(UiEvent.ShowToast("Change request $id approved & executed!"))
        }
    }

    fun rejectChangeRequest(id: String, reason: String) {
        viewModelScope.launch {
            repository.rejectChangeRequest(id, reason)
            _uiEvents.emit(UiEvent.ShowToast("Change request $id rejected"))
        }
    }

    // Deployments
    fun triggerDeployment(projectId: String, projectName: String, env: EnvironmentType, commitSha: String, branch: String) {
        viewModelScope.launch {
            repository.triggerVercelDeployment(projectId, projectName, env, commitSha, branch)
            _uiEvents.emit(UiEvent.ShowToast("Triggered ${env.label} deployment on Vercel!"))
        }
    }

    // Integrations
    fun pingIntegration(id: String) {
        viewModelScope.launch {
            val res = repository.pingIntegration(id)
            res.onSuccess { latency ->
                _uiEvents.emit(UiEvent.ShowToast("$id healthy (${latency}ms)"))
            }
        }
    }

    // Media
    fun addMediaAsset(projectId: String, title: String, url: String, alt: String, category: String) {
        viewModelScope.launch {
            repository.addMediaAsset(projectId, title, url, alt, category)
            _uiEvents.emit(UiEvent.ShowToast("Media asset registered"))
        }
    }

    fun deleteMediaAsset(id: String) {
        viewModelScope.launch {
            repository.deleteMediaAsset(id)
            _uiEvents.emit(UiEvent.ShowToast("Media reference removed"))
        }
    }

    // Setup Wizard Actions
    fun updateIntegrationField(serviceId: String, key: String, value: String) {
        repository.updateIntegrationField(serviceId, key, value)
    }

    fun testAndVerifyIntegration(serviceId: String) {
        viewModelScope.launch {
            val result = repository.testAndVerifyIntegration(serviceId)
            result.onSuccess { s ->
                _uiEvents.emit(UiEvent.ShowToast("${s.name} verified successfully! Latency: ${s.latencyMs}ms"))
            }.onFailure { err ->
                _uiEvents.emit(UiEvent.ShowError(err.message ?: "Verification failed"))
            }
        }
    }

    fun toggleIntegrationActive(serviceId: String, activate: Boolean) {
        viewModelScope.launch {
            val result = repository.toggleIntegrationActive(serviceId, activate)
            result.onSuccess { s ->
                _uiEvents.emit(UiEvent.ShowToast("${s.name} is now ${s.state.label}"))
            }.onFailure { err ->
                _uiEvents.emit(UiEvent.ShowError(err.message ?: "Activation failed"))
            }
        }
    }

    fun resetIntegration(serviceId: String) {
        viewModelScope.launch {
            repository.resetIntegration(serviceId)
            _uiEvents.emit(UiEvent.ShowToast("Integration credentials cleared and reset."))
        }
    }

    // Code & Git helpers
    fun getRepositoryCommits() = repository.getRepositoryCommits()
    fun getRepositoryFiles() = repository.getRepositoryFiles()
    fun getDatabaseTables() = repository.getDatabaseTables()
}
