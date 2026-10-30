package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.DatabaseProvider
import com.example.domain.model.EnvironmentType
import com.example.domain.model.HostingProvider
import com.example.domain.model.Project
import com.example.domain.model.ProjectType
import com.example.ui.components.CyberCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberCyanDark
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MasterAdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
    viewModel: MasterAdminViewModel,
    onNavigateToProject: (String) -> Unit
) {
    val projects by viewModel.filteredProjects.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.projectTypeFilter.collectAsState()
    val context = LocalContext.current

    var showAddDialog by remember { mutableStateOf(false) }
    var projectToArchive by remember { mutableStateOf<Project?>(null) }

    Scaffold(
        containerColor = CyberBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = CyberCyan,
                contentColor = Color(0xFF002026),
                modifier = Modifier.testTag("add_project_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Project")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .testTag("projects_screen")
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Projects & Websites",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            Text(
                text = "Manage repositories, hosting environments, and connected databases.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchQuery.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("project_search_field"),
                placeholder = { Text("Search by name, URL, or keywords...", color = TextMuted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CyberCyan) },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = CyberSurfaceElevated,
                    unfocusedContainerColor = CyberSurfaceElevated,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedIndicatorColor = CyberCyan,
                    unfocusedIndicatorColor = CyberBorder
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { viewModel.projectTypeFilter.value = null },
                    label = { Text("All (${projects.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberCyanDark,
                        selectedLabelColor = CyberCyan,
                        containerColor = CyberSurfaceElevated,
                        labelColor = TextSecondary
                    )
                )
                ProjectType.values().forEach { type ->
                    FilterChip(
                        selected = selectedFilter == type,
                        onClick = {
                            viewModel.projectTypeFilter.value = if (selectedFilter == type) null else type
                        },
                        label = { Text(type.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberCyanDark,
                            selectedLabelColor = CyberCyan,
                            containerColor = CyberSurfaceElevated,
                            labelColor = TextSecondary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Projects List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(projects, key = { it.id }) { project ->
                    CyberCard(
                        modifier = Modifier.testTag("project_card_${project.id}"),
                        onClick = { onNavigateToProject(project.id) },
                        borderColor = if (project.id == "proj_mohitport") CyberCyan.copy(alpha = 0.6f) else CyberBorder
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CyberCyan.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (project.projectType == ProjectType.PORTFOLIO) Icons.Default.Terminal else Icons.Default.Code,
                                        contentDescription = null,
                                        tint = CyberCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = project.name,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${project.projectType.label} • ${project.environment.label}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                            }
                            StatusBadge(status = project.healthStatus.label)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = project.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Repo: ${project.repositoryUrl.substringAfterLast("/")}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                            Row {
                                IconButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(project.url))
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.OpenInBrowser, contentDescription = "Open in browser", tint = CyberCyan, modifier = Modifier.size(18.dp))
                                }
                                IconButton(
                                    onClick = { projectToArchive = project },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.Archive, contentDescription = "Archive", tint = TextMuted, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Register New Project Dialog
    if (showAddDialog) {
        AddProjectDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, desc, url, type, env, repo, host, db, apiBaseUrl, apiAuthHeader, apiAuthToken, dbUri, vercelId, branch ->
                viewModel.addProject(
                    name = name,
                    description = desc,
                    url = url,
                    type = type,
                    environment = env,
                    repo = repo,
                    hosting = host,
                    db = db,
                    apiBaseUrl = apiBaseUrl,
                    apiAuthHeader = apiAuthHeader,
                    apiAuthToken = apiAuthToken,
                    dbConnectionString = dbUri,
                    vercelProjectId = vercelId,
                    gitBranch = branch
                )
                showAddDialog = false
            }
        )
    }

    // Archive Confirmation Dialog (Strict security & destructive safeguard requirement)
    projectToArchive?.let { project ->
        AlertDialog(
            onDismissRequest = { projectToArchive = null },
            title = { Text("Archive Project Registration?", color = TextPrimary) },
            text = {
                Text(
                    "Archiving '${project.name}' removes it from the local active control center.\n\nNOTE: External cloud resources (GitHub repo, Vercel deployments, databases) are strictly PRESERVED and untouched.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.archiveProject(project.id)
                        projectToArchive = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusRed)
                ) {
                    Text("Archive Registration")
                }
            },
            dismissButton = {
                TextButton(onClick = { projectToArchive = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = CyberSurfaceElevated
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, ProjectType, EnvironmentType, String, HostingProvider, DatabaseProvider, String, String, String, String, String, String) -> Unit
) {
    var currentStep by remember { mutableStateOf(0) } // 0: General, 1: API Config, 2: Database & Hosting

    // Step 0: General
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("https://") }
    var selectedType by remember { mutableStateOf(ProjectType.WEB_APP) }
    var selectedEnv by remember { mutableStateOf(EnvironmentType.PRODUCTION) }

    // Step 1: API Gateway
    var apiBaseUrl by remember { mutableStateOf("") }
    var apiAuthHeader by remember { mutableStateOf("Authorization") }
    var apiAuthToken by remember { mutableStateOf("") }

    // Step 2: Database & Hosting
    var selectedDb by remember { mutableStateOf(DatabaseProvider.POSTGRESQL) }
    var dbConnectionString by remember { mutableStateOf("") }
    var selectedHost by remember { mutableStateOf(HostingProvider.VERCEL) }
    var vercelProjectId by remember { mutableStateOf("") }
    var repo by remember { mutableStateOf("github.com/mohityadav/") }
    var branch by remember { mutableStateOf("main") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Add Website / Service",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Text(
                    text = when (currentStep) {
                        0 -> "Step 1 of 3: General Information"
                        1 -> "Step 2 of 3: API Gateway & Auth"
                        else -> "Step 3 of 3: Database & Deployment"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = CyberCyan
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Steps indicator row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("1. General", "2. API Gateway", "3. Cloud & DB").forEachIndexed { index, label ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(26.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (currentStep == index) CyberCyanDark else CyberSurfaceElevated)
                                .border(1.dp, if (currentStep == index) CyberCyan else CyberBorder, RoundedCornerShape(6.dp))
                                .clickable { currentStep = index },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (currentStep == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (currentStep == index) CyberCyan else TextSecondary
                            )
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    when (currentStep) {
                        0 -> {
                            item {
                                OutlinedTextField(
                                    value = name,
                                    onValueChange = {
                                        name = it
                                        if (apiBaseUrl.isBlank() && it.isNotBlank()) {
                                            apiBaseUrl = "https://api.${it.lowercase().replace(" ", "-")}.com/v1"
                                        }
                                        if (vercelProjectId.isBlank() && it.isNotBlank()) {
                                            vercelProjectId = "prj_${it.lowercase().replace(" ", "-")}"
                                        }
                                    },
                                    label = { Text("Website / Service Name") },
                                    placeholder = { Text("e.g. NextGen Web Service") },
                                    modifier = Modifier.fillMaxWidth().testTag("add_project_name"),
                                    singleLine = true
                                )
                            }
                            item {
                                OutlinedTextField(
                                    value = description,
                                    onValueChange = { description = it },
                                    label = { Text("Purpose & Description") },
                                    modifier = Modifier.fillMaxWidth().testTag("add_project_desc")
                                )
                            }
                            item {
                                OutlinedTextField(
                                    value = url,
                                    onValueChange = { url = it },
                                    label = { Text("Live Domain URL") },
                                    modifier = Modifier.fillMaxWidth().testTag("add_project_url"),
                                    singleLine = true
                                )
                            }
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    ProjectType.values().forEach { type ->
                                        FilterChip(
                                            selected = selectedType == type,
                                            onClick = { selectedType = type },
                                            label = { Text(type.label, fontSize = 11.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = CyberCyanDark,
                                                selectedLabelColor = CyberCyan,
                                                containerColor = CyberSurfaceElevated,
                                                labelColor = TextSecondary
                                            )
                                        )
                                    }
                                }
                            }
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    EnvironmentType.values().forEach { env ->
                                        FilterChip(
                                            selected = selectedEnv == env,
                                            onClick = { selectedEnv = env },
                                            label = { Text(env.label, fontSize = 11.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = CyberCyanDark,
                                                selectedLabelColor = CyberCyan,
                                                containerColor = CyberSurfaceElevated,
                                                labelColor = TextSecondary
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        1 -> {
                            item {
                                Text(
                                    text = "Connect this website's backend API endpoints for live status checks and CMS synchronization:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                            item {
                                OutlinedTextField(
                                    value = apiBaseUrl,
                                    onValueChange = { apiBaseUrl = it },
                                    label = { Text("API Base URL (HTTPS)") },
                                    placeholder = { Text("https://api.mywebsite.com/v1") },
                                    modifier = Modifier.fillMaxWidth().testTag("add_project_api_url"),
                                    singleLine = true
                                )
                            }
                            item {
                                OutlinedTextField(
                                    value = apiAuthHeader,
                                    onValueChange = { apiAuthHeader = it },
                                    label = { Text("Auth Header (Authorization / X-API-Key)") },
                                    modifier = Modifier.fillMaxWidth().testTag("add_project_auth_header"),
                                    singleLine = true
                                )
                            }
                            item {
                                OutlinedTextField(
                                    value = apiAuthToken,
                                    onValueChange = { apiAuthToken = it },
                                    label = { Text("API Secret / Bearer Token") },
                                    modifier = Modifier.fillMaxWidth().testTag("add_project_auth_token"),
                                    singleLine = true
                                )
                            }
                        }
                        2 -> {
                            item {
                                Text(
                                    text = "Database Connection & Deployment Binding:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    DatabaseProvider.values().forEach { db ->
                                        FilterChip(
                                            selected = selectedDb == db,
                                            onClick = { selectedDb = db },
                                            label = { Text(db.label, fontSize = 11.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = CyberCyanDark,
                                                selectedLabelColor = CyberCyan,
                                                containerColor = CyberSurfaceElevated,
                                                labelColor = TextSecondary
                                            )
                                        )
                                    }
                                }
                            }
                            item {
                                OutlinedTextField(
                                    value = dbConnectionString,
                                    onValueChange = { dbConnectionString = it },
                                    label = { Text("Database URI / Project Connection String") },
                                    placeholder = { Text("postgresql://user:pass@host:5432/db") },
                                    modifier = Modifier.fillMaxWidth().testTag("add_project_db_uri"),
                                    singleLine = true
                                )
                            }
                            item {
                                OutlinedTextField(
                                    value = vercelProjectId,
                                    onValueChange = { vercelProjectId = it },
                                    label = { Text("Hosting / Vercel Project ID") },
                                    modifier = Modifier.fillMaxWidth().testTag("add_project_vercel_id"),
                                    singleLine = true
                                )
                            }
                            item {
                                OutlinedTextField(
                                    value = repo,
                                    onValueChange = { repo = it },
                                    label = { Text("GitHub Repository Slug") },
                                    modifier = Modifier.fillMaxWidth().testTag("add_project_repo"),
                                    singleLine = true
                                )
                            }
                            item {
                                OutlinedTextField(
                                    value = branch,
                                    onValueChange = { branch = it },
                                    label = { Text("Target Git Branch") },
                                    modifier = Modifier.fillMaxWidth().testTag("add_project_branch"),
                                    singleLine = true
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (currentStep < 2) {
                Button(
                    onClick = { currentStep += 1 },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF002026))
                ) {
                    Text("Next Step →", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            onConfirm(
                                name, description, url, selectedType, selectedEnv, repo, selectedHost, selectedDb,
                                apiBaseUrl, apiAuthHeader, apiAuthToken, dbConnectionString, vercelProjectId, branch
                            )
                        }
                    },
                    enabled = name.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF002026)),
                    modifier = Modifier.testTag("submit_add_website_btn")
                ) {
                    Text("Register & Save Website", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            if (currentStep > 0) {
                TextButton(onClick = { currentStep -= 1 }) {
                    Text("← Back", color = TextSecondary)
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        },
        containerColor = CyberSurfaceElevated
    )
}
