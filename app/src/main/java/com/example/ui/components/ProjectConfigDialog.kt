package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.DatabaseProvider
import com.example.domain.model.HostingProvider
import com.example.domain.model.Project
import com.example.domain.model.ProjectApiTestResult
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberCyanDark
import com.example.ui.theme.CyberIndigo
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MasterAdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectConfigDialog(
    project: Project,
    viewModel: MasterAdminViewModel,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: API Config, 1: Database, 2: Hosting & Git

    // API Config State
    var apiBaseUrl by remember { mutableStateOf(project.apiBaseUrl.ifBlank { "https://api.${project.id.removePrefix("proj_")}.internal/v1" }) }
    var apiAuthHeader by remember { mutableStateOf(project.apiAuthHeader.ifBlank { "Authorization" }) }
    var apiAuthToken by remember { mutableStateOf(project.apiAuthToken) }
    var apiTestResult by remember { mutableStateOf<ProjectApiTestResult?>(null) }
    var isTestingApi by remember { mutableStateOf(false) }

    // Database Config State
    var dbProvider by remember { mutableStateOf(project.databaseProvider) }
    var dbConnectionString by remember { mutableStateOf(project.dbConnectionString.ifBlank { "postgresql://user:pass@ep-pool.neon.tech/${project.id}?sslmode=require" }) }
    var dbTestResult by remember { mutableStateOf<String?>(null) }
    var isTestingDb by remember { mutableStateOf(false) }

    // Hosting & Git State
    var vercelProjectId by remember { mutableStateOf(project.vercelProjectId.ifBlank { "prj_${project.id.removePrefix("proj_")}" }) }
    var gitBranch by remember { mutableStateOf(project.gitBranch.ifBlank { "main" }) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Configure Website & APIs",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    StatusBadge(status = project.environment.label)
                }
                Text(
                    text = "${project.name} (${project.id})",
                    style = MaterialTheme.typography.bodySmall,
                    color = CyberCyan
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("project_config_dialog")
            ) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = CyberSurfaceElevated,
                    contentColor = CyberCyan,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = CyberCyan
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("API Gateway", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Database", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Hosting/Git", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    when (selectedTab) {
                        0 -> {
                            // --- API Config Tab ---
                            item {
                                Text(
                                    text = "Configure REST API Base URL and Authentication Headers without touching source code:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                            item {
                                OutlinedTextField(
                                    value = apiBaseUrl,
                                    onValueChange = { apiBaseUrl = it },
                                    label = { Text("API Base URL (HTTPS)") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("config_api_url"),
                                    singleLine = true,
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = CyberSurfaceElevated,
                                        unfocusedContainerColor = CyberSurfaceElevated,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedIndicatorColor = CyberCyan,
                                        unfocusedIndicatorColor = CyberBorder
                                    )
                                )
                            }
                            item {
                                OutlinedTextField(
                                    value = apiAuthHeader,
                                    onValueChange = { apiAuthHeader = it },
                                    label = { Text("Auth Header (e.g. Authorization or X-API-Key)") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("config_api_header"),
                                    singleLine = true,
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = CyberSurfaceElevated,
                                        unfocusedContainerColor = CyberSurfaceElevated,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedIndicatorColor = CyberCyan,
                                        unfocusedIndicatorColor = CyberBorder
                                    )
                                )
                            }
                            item {
                                OutlinedTextField(
                                    value = apiAuthToken,
                                    onValueChange = { apiAuthToken = it },
                                    label = { Text("Bearer Token / Secret Key") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("config_api_token"),
                                    visualTransformation = PasswordVisualTransformation(),
                                    singleLine = true,
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = CyberSurfaceElevated,
                                        unfocusedContainerColor = CyberSurfaceElevated,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedIndicatorColor = CyberCyan,
                                        unfocusedIndicatorColor = CyberBorder
                                    )
                                )
                            }
                            item {
                                // Live Test Connection Button
                                OutlinedButton(
                                    onClick = {
                                        isTestingApi = true
                                        viewModel.testProjectApiConnection(
                                            projectId = project.id,
                                            url = apiBaseUrl,
                                            authHeader = apiAuthHeader,
                                            token = apiAuthToken
                                        ) { result ->
                                            isTestingApi = false
                                            apiTestResult = result
                                        }
                                    },
                                    enabled = !isTestingApi && apiBaseUrl.isNotBlank(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("test_api_connection_btn"),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan)
                                ) {
                                    if (isTestingApi) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = CyberCyan, strokeWidth = 2.dp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Testing Handshake...")
                                    } else {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Test API Connection")
                                    }
                                }
                            }
                            apiTestResult?.let { res ->
                                item {
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (res.isSuccess) StatusGreen.copy(alpha = 0.15f) else StatusRed.copy(alpha = 0.15f)
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = if (res.isSuccess) "✓ Handshake OK (HTTP ${res.statusCode})" else "✗ Connection Failed",
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (res.isSuccess) StatusGreen else StatusRed,
                                                    fontSize = 12.sp
                                                )
                                                Text(
                                                    text = "${res.latencyMs}ms",
                                                    color = TextMuted,
                                                    fontSize = 11.sp,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            }
                                            Text(
                                                text = res.message,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        1 -> {
                            // --- Database Config Tab ---
                            item {
                                Text(
                                    text = "Connect managed PostgreSQL, Neon, Supabase, or Firestore directly:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                            item {
                                OutlinedTextField(
                                    value = dbConnectionString,
                                    onValueChange = { dbConnectionString = it },
                                    label = { Text("Database Connection String URI") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("config_db_uri"),
                                    singleLine = true,
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = CyberSurfaceElevated,
                                        unfocusedContainerColor = CyberSurfaceElevated,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedIndicatorColor = CyberCyan,
                                        unfocusedIndicatorColor = CyberBorder
                                    )
                                )
                            }
                            item {
                                OutlinedButton(
                                    onClick = {
                                        isTestingDb = true
                                        viewModel.testProjectDatabaseConnection(
                                            projectId = project.id,
                                            provider = dbProvider,
                                            connectionUri = dbConnectionString
                                        ) { msg ->
                                            isTestingDb = false
                                            dbTestResult = msg
                                        }
                                    },
                                    enabled = !isTestingDb && dbConnectionString.isNotBlank(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("test_db_connection_btn"),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan)
                                ) {
                                    if (isTestingDb) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = CyberCyan, strokeWidth = 2.dp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Verifying Database...")
                                    } else {
                                        Icon(Icons.Default.Storage, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Test Database Connection")
                                    }
                                }
                            }
                            dbTestResult?.let { msg ->
                                item {
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = StatusGreen.copy(alpha = 0.15f)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(text = msg, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                                        }
                                    }
                                }
                            }
                        }
                        2 -> {
                            // --- Hosting & Git Tab ---
                            item {
                                OutlinedTextField(
                                    value = vercelProjectId,
                                    onValueChange = { vercelProjectId = it },
                                    label = { Text("Vercel Project ID / Deployment Slug") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("config_vercel_id"),
                                    singleLine = true,
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = CyberSurfaceElevated,
                                        unfocusedContainerColor = CyberSurfaceElevated,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedIndicatorColor = CyberCyan,
                                        unfocusedIndicatorColor = CyberBorder
                                    )
                                )
                            }
                            item {
                                OutlinedTextField(
                                    value = gitBranch,
                                    onValueChange = { gitBranch = it },
                                    label = { Text("Production Git Branch") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("config_git_branch"),
                                    singleLine = true,
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = CyberSurfaceElevated,
                                        unfocusedContainerColor = CyberSurfaceElevated,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedIndicatorColor = CyberCyan,
                                        unfocusedIndicatorColor = CyberBorder
                                    )
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.updateProjectConfiguration(
                        projectId = project.id,
                        apiBaseUrl = apiBaseUrl,
                        apiAuthHeader = apiAuthHeader,
                        apiAuthToken = apiAuthToken,
                        dbConnectionString = dbConnectionString,
                        vercelProjectId = vercelProjectId,
                        gitBranch = gitBranch
                    )
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberCyan,
                    contentColor = Color(0xFF002026)
                ),
                modifier = Modifier.testTag("save_project_config_btn")
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Configurations", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = TextSecondary)
            }
        },
        containerColor = CyberSurfaceElevated
    )
}
