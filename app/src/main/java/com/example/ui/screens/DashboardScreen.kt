package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Approval
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.domain.model.ChangeStatus
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
fun DashboardScreen(
    viewModel: MasterAdminViewModel,
    onNavigateToProject: (String) -> Unit,
    onNavigateToChanges: () -> Unit,
    onNavigateToDeployments: () -> Unit,
    onNavigateToProjects: () -> Unit,
    onNavigateToAudit: () -> Unit,
    onNavigateToSetup: () -> Unit
) {
    val projects by viewModel.rawProjects.collectAsState()
    val changeRequests by viewModel.changeRequests.collectAsState()
    val deployments by viewModel.deployments.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()
    val services by viewModel.integrationServices.collectAsState()
    val context = LocalContext.current

    val pendingCount = changeRequests.count { it.status == ChangeStatus.PENDING }
    val featuredProject = projects.firstOrNull { it.id == "proj_mohitport" } ?: projects.firstOrNull()
    val activeServicesCount = services.count { it.state == com.example.domain.model.IntegrationState.ACTIVE }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(horizontal = 16.dp)
            .testTag("dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // System Control Header Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CyberCyanDark.copy(alpha = 0.4f))
                    .border(1.dp, CyberCyan.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(StatusGreen)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SYSTEM READY • CONFIGURE AS NEEDED",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = StatusGreen
                            )
                        }
                        Text(
                            text = "OWNER: ${viewModel.ownerName.uppercase()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF151515))
                                .border(1.dp, CyberCyan, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.foundation.Image(
                                painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_mohitos_logo),
                                contentDescription = "MohitOS Emblem",
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "MohitOS",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                ),
                                color = CyberCyan
                            )
                            Text(
                                text = "YOUR DIGITAL CONTROL CENTER",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp
                                ),
                                color = TextSecondary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Unified command center for portfolio websites, APIs, databases & Vercel deployments.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }

        // Master Setup & Integration Center Callout
        item {
            CyberCard(
                borderColor = CyberCyan,
                backgroundColor = Color(0xFF0F172A),
                onClick = onNavigateToSetup
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyberCyanDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Master Setup & Integration Center",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Text(
                                text = "$activeServicesCount of ${services.size} Services Active • Guided Activation Wizard",
                                style = MaterialTheme.typography.bodySmall,
                                color = CyberCyan
                            )
                        }
                    }
                    Button(
                        onClick = onNavigateToSetup,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF002026)),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Configure", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Metrics Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "PROJECTS",
                    value = projects.size.toString(),
                    subtitle = "Active & Configured",
                    icon = Icons.Default.Folder,
                    iconTint = CyberCyan,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "PENDING APPROVALS",
                    value = pendingCount.toString(),
                    subtitle = if (pendingCount > 0) "Requires Review" else "All Cleared",
                    icon = Icons.Default.Approval,
                    iconTint = if (pendingCount > 0) com.example.ui.theme.StatusAmber else StatusGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "HOSTING",
                    value = "Vercel",
                    subtitle = "Edge Network Live",
                    icon = Icons.Default.Public,
                    iconTint = StatusGreen,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "SYNC LATENCY",
                    value = "38 ms",
                    subtitle = "PostgreSQL Connected",
                    icon = Icons.Default.Speed,
                    iconTint = CyberIndigo,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Featured Portfolio Command Card
        if (featuredProject != null) {
            item {
                SectionHeader(
                    title = "Primary Website Hub",
                    badgeText = "VERCEL PROD",
                    action = {
                        Text(
                            text = "Manage All",
                            color = CyberCyan,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier
                                .clickable { onNavigateToProjects() }
                                .padding(4.dp)
                        )
                    }
                )
                CyberCard(
                    modifier = Modifier.testTag("featured_project_card"),
                    onClick = { onNavigateToProject(featuredProject.id) },
                    borderColor = CyberCyan.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CyberCyan.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Terminal,
                                    contentDescription = null,
                                    tint = CyberCyan,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = featuredProject.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Text(
                                    text = featuredProject.url,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CyberCyan,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        StatusBadge(status = featuredProject.healthStatus.label)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = featuredProject.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onNavigateToProject(featuredProject.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF002026)),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("open_project_hub_btn")
                        ) {
                            Text("Open Control Hub", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(featuredProject.url))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                            modifier = Modifier.testTag("visit_website_btn")
                        ) {
                            Icon(Icons.Default.OpenInBrowser, contentDescription = "Open Website", modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        // Pending Change Requests Section
        item {
            SectionHeader(
                title = "Pending Approvals",
                badgeText = if (pendingCount > 0) "$pendingCount Awaiting" else "Clean",
                action = {
                    Text(
                        text = "View All",
                        color = CyberCyan,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .clickable { onNavigateToChanges() }
                            .padding(4.dp)
                    )
                }
            )
        }

        val pendingRequests = changeRequests.filter { it.status == ChangeStatus.PENDING }.take(2)
        if (pendingRequests.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberSurfaceElevated)
                        .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "No pending approval requests. All changes synchronized.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        } else {
            items(pendingRequests) { req ->
                CyberCard(
                    modifier = Modifier.testTag("pending_request_item_${req.id}"),
                    onClick = { onNavigateToChanges() }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = req.title,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = TextPrimary
                        )
                        StatusBadge(status = req.riskLevel.label)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = req.diffSummary,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Recent Deployments Section
        item {
            SectionHeader(
                title = "Recent Deployments",
                badgeText = "Vercel",
                action = {
                    Text(
                        text = "History",
                        color = CyberCyan,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .clickable { onNavigateToDeployments() }
                            .padding(4.dp)
                    )
                }
            )
        }

        val recentDeployments = deployments.take(2)
        items(recentDeployments) { dpl ->
            CyberCard(
                onClick = { onNavigateToDeployments() }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = dpl.commitMessage,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "SHA: ${dpl.commitSha} • ${dpl.branch} • ${dpl.environment.label}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    }
                    StatusBadge(status = dpl.status.label)
                }
            }
        }

        // Recent Audit Activity Section
        item {
            SectionHeader(
                title = "Audit Trail",
                badgeText = "Live",
                action = {
                    Text(
                        text = "Full Log",
                        color = CyberCyan,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .clickable { onNavigateToAudit() }
                            .padding(4.dp)
                    )
                }
            )
        }

        val recentAudits = auditLogs.take(3)
        items(recentAudits) { audit ->
            CyberCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberIndigo.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = CyberIndigo, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = audit.action,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = audit.details,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                    StatusBadge(status = audit.status)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
