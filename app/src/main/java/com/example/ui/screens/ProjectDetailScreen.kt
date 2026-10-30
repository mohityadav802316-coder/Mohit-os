package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.ui.components.ProjectConfigDialog
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
import com.example.ui.components.CyberCard
import com.example.ui.components.MetricCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberCyanDark
import com.example.ui.theme.CyberIndigo
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MasterAdminViewModel

@Composable
fun ProjectDetailScreen(
    projectId: String,
    viewModel: MasterAdminViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val projects by viewModel.rawProjects.collectAsState()
    val activeTab by viewModel.activeProjectTab.collectAsState()
    val context = LocalContext.current

    val project = projects.firstOrNull { it.id == projectId }
    var showConfigDialog by remember { mutableStateOf(false) }

    if (project == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CyberBackground),
            contentAlignment = Alignment.Center
        ) {
            Text("Project not found", color = TextSecondary)
        }
        return
    }

    val tabTitles = listOf("Overview", "Content CMS", "Code & Git", "Deployments", "Database", "Media", "Integrations")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .testTag("project_detail_screen")
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("project_back_btn")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary,
                    maxLines = 1
                )
                Text(
                    text = "${project.projectType.label} • ${project.hostingProvider.label}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            StatusBadge(status = project.healthStatus.label)
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(
                onClick = { showConfigDialog = true },
                modifier = Modifier.testTag("open_project_config_btn")
            ) {
                Icon(Icons.Default.Tune, contentDescription = "Configure Project", tint = CyberCyan)
            }
            IconButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(project.url))
                    context.startActivity(intent)
                }
            ) {
                Icon(Icons.Default.OpenInBrowser, contentDescription = "Open live URL", tint = CyberCyan)
            }
        }

        // Tab Row
        ScrollableTabRow(
            selectedTabIndex = activeTab,
            containerColor = CyberSurfaceElevated,
            contentColor = CyberCyan,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                    color = CyberCyan
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = activeTab == index,
                    onClick = { viewModel.setProjectTab(index) },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (activeTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (activeTab == index) CyberCyan else TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        // Tab Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            when (activeTab) {
                0 -> ProjectOverviewTab(project, viewModel, onOpenConfig = { showConfigDialog = true })
                1 -> ContentManagerScreen(viewModel, project.id)
                2 -> CodeManagerScreen(viewModel, project.id)
                3 -> DeploymentsScreen(viewModel, project.id)
                4 -> DatabaseManagerScreen(viewModel)
                5 -> MediaManagerScreen(viewModel, project.id)
                6 -> IntegrationsScreen(viewModel, project.id)
            }
        }
    }

    if (showConfigDialog) {
        ProjectConfigDialog(
            project = project,
            viewModel = viewModel,
            onDismiss = { showConfigDialog = false }
        )
    }
}

@Composable
fun ProjectOverviewTab(
    project: com.example.domain.model.Project,
    viewModel: MasterAdminViewModel,
    onOpenConfig: () -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            CyberCard(borderColor = CyberCyan.copy(alpha = 0.4f)) {
                Text(
                    text = "Project Summary",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = project.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("PRODUCTION URL", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text(project.url, style = MaterialTheme.typography.bodySmall, color = CyberCyan, fontFamily = FontFamily.Monospace)
                    }
                    Column {
                        Text("HOSTING", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text(project.hostingProvider.label, style = MaterialTheme.typography.bodySmall, color = StatusGreen)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("REPOSITORY", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text(project.repositoryUrl, style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }

        item {
            CyberCard(borderColor = CyberIndigo.copy(alpha = 0.5f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberIndigo.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Self-Configured Infrastructure",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Text(
                                text = "Zero manual code editing required for APIs & DBs",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("API GATEWAY", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text(
                            text = if (project.apiBaseUrl.isNotBlank()) project.apiBaseUrl else "https://api.mohitport.com/v1",
                            style = MaterialTheme.typography.bodySmall,
                            color = CyberCyan,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("DEPLOYMENT & DB", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text(
                            text = "${project.hostingProvider.label} • ${project.databaseProvider.label}",
                            style = MaterialTheme.typography.bodySmall,
                            color = StatusGreen,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onOpenConfig,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyanDark, contentColor = CyberCyan),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("overview_configure_project_btn")
                ) {
                    Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Configure Project & Integrations", fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            SectionHeader(title = "Quick Navigation Actions")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.setProjectTab(1) }, // CMS tab
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF002026)),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit CMS", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { viewModel.setProjectTab(3) }, // Deployments tab
                    colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceElevated, contentColor = TextPrimary),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Deployments")
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.setProjectTab(2) }, // Code tab
                    colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceElevated, contentColor = TextPrimary),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Code, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Source Code")
                }
                Button(
                    onClick = { viewModel.setProjectTab(4) }, // Database tab
                    colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceElevated, contentColor = TextPrimary),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Storage, contentDescription = null, tint = CyberIndigo, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Database")
                }
            }
        }

        item {
            SectionHeader(title = "Health & Verification Diagnostics")
            CyberCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Vercel Edge Network", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                        Text("SSL/TLS verified, global CDN active", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                    StatusBadge(status = "HEALTHY")
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("PostgreSQL Data Source", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                        Text("Read/Write connection verified (42ms)", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                    StatusBadge(status = "CONNECTED")
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("GitHub Webhook Sync", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                        Text("Automated preview deployment trigger", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                    StatusBadge(status = "CONNECTED")
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
