package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.AuditLogEntity
import com.example.data.local.entity.ChangeRequestEntity
import com.example.data.local.entity.DeploymentEntity
import com.example.data.local.entity.DynamicContentFieldEntity
import com.example.data.local.entity.IntegrationEntity
import com.example.data.local.entity.MediaAssetEntity
import com.example.data.local.entity.OwnerConfigEntity
import com.example.data.local.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MasterAdminDao {
    // Projects
    @Query("SELECT * FROM projects WHERE isArchived = 0 ORDER BY lastSyncTimestamp DESC")
    fun getAllActiveProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: String): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjects(projects: List<ProjectEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProjectById(id: String)

    // Change Requests
    @Query("SELECT * FROM change_requests ORDER BY createdAt DESC")
    fun getAllChangeRequests(): Flow<List<ChangeRequestEntity>>

    @Query("SELECT * FROM change_requests WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun getChangeRequestsForProject(projectId: String): Flow<List<ChangeRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChangeRequests(requests: List<ChangeRequestEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChangeRequest(request: ChangeRequestEntity)

    @Query("UPDATE change_requests SET status = :status, reviewedAt = :reviewedAt, reviewerNotes = :notes WHERE id = :id")
    suspend fun updateChangeRequestStatus(id: String, status: String, reviewedAt: Long, notes: String?)

    // Deployments
    @Query("SELECT * FROM deployments ORDER BY timestamp DESC")
    fun getAllDeployments(): Flow<List<DeploymentEntity>>

    @Query("SELECT * FROM deployments WHERE projectId = :projectId ORDER BY timestamp DESC")
    fun getDeploymentsForProject(projectId: String): Flow<List<DeploymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeployments(deployments: List<DeploymentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeployment(deployment: DeploymentEntity)

    // Integrations
    @Query("SELECT * FROM integrations ORDER BY provider ASC")
    fun getAllIntegrations(): Flow<List<IntegrationEntity>>

    @Query("SELECT * FROM integrations WHERE projectId = :projectId")
    fun getIntegrationsForProject(projectId: String): Flow<List<IntegrationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntegrations(integrations: List<IntegrationEntity>)

    @Query("SELECT * FROM integrations WHERE id = :id")
    suspend fun getIntegrationById(id: String): IntegrationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntegration(integration: IntegrationEntity)

    // Audit Logs
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLogs(logs: List<AuditLogEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)

    // Media Assets
    @Query("SELECT * FROM media_assets ORDER BY uploadedAt DESC")
    fun getAllMediaAssets(): Flow<List<MediaAssetEntity>>

    @Query("SELECT * FROM media_assets WHERE projectId = :projectId ORDER BY uploadedAt DESC")
    fun getMediaAssetsForProject(projectId: String): Flow<List<MediaAssetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaAssets(assets: List<MediaAssetEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaAsset(asset: MediaAssetEntity)

    @Query("DELETE FROM media_assets WHERE id = :id")
    suspend fun deleteMediaAssetById(id: String)

    // Owner Configuration
    @Query("SELECT * FROM owner_config WHERE id = 'primary_owner' LIMIT 1")
    fun getOwnerConfig(): Flow<OwnerConfigEntity?>

    @Query("SELECT * FROM owner_config WHERE id = 'primary_owner' LIMIT 1")
    suspend fun getOwnerConfigOnce(): OwnerConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOwnerConfig(config: OwnerConfigEntity)

    // Dynamic CMS Content Fields
    @Query("SELECT * FROM dynamic_content_fields WHERE projectId = :projectId ORDER BY updatedAt ASC")
    fun getDynamicFieldsForProject(projectId: String): Flow<List<DynamicContentFieldEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDynamicField(field: DynamicContentFieldEntity)

    @Query("UPDATE dynamic_content_fields SET fieldValue = :newValue, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateDynamicFieldValue(id: String, newValue: String, updatedAt: Long)

    @Query("DELETE FROM dynamic_content_fields WHERE id = :id")
    suspend fun deleteDynamicFieldById(id: String)

    // In-App Project Custom Configuration Update
    @Query("UPDATE projects SET apiBaseUrl = :apiBaseUrl, apiAuthHeader = :apiAuthHeader, apiAuthToken = :apiAuthToken, dbConnectionString = :dbConnectionString, vercelProjectId = :vercelProjectId, gitBranch = :gitBranch WHERE id = :id")
    suspend fun updateProjectConfig(
        id: String,
        apiBaseUrl: String,
        apiAuthHeader: String,
        apiAuthToken: String,
        dbConnectionString: String,
        vercelProjectId: String,
        gitBranch: String
    )
}
