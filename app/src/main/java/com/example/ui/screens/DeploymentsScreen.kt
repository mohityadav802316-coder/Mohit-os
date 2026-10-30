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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.domain.model.Deployment
import com.example.domain.model.EnvironmentType
import com.example.ui.components.CyberCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberCyanDark
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MasterAdminViewModel

@Composable
fun DeploymentsScreen(
    viewModel: MasterAdminViewModel,
    projectId: String? = null
) {
    val allDeployments by viewModel.deployments.collectAsState()
    val deployments = if (projectId != null) {
        allDeployments.filter { it.projectId == projectId }
    } else {
        allDeployments
    }

    val context = LocalContext.current
    var showTriggerDialog by remember { mutableStateOf(false) }
    var selectedDeploymentForLogs by remember { mutableStateOf<Deployment?>(null) }

    val liveProd = deployments.firstOrNull { it.environment == EnvironmentType.PRODUCTION }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(top = 12.dp)
            .testTag("deployments_screen")
    ) {
        // Active Vercel Release Status
        CyberCard(borderColor = StatusGreen.copy(alpha = 0.5f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(StatusGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CloudDone, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Vercel Production Deployment",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = liveProd?.deploymentUrl ?: "https://mohitport.vercel.app",
                            style = MaterialTheme.typography.bodySmall,
                            color = CyberCyan,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    }
                }
                StatusBadge(status = liveProd?.status?.label ?: "READY")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Commit: ${liveProd?.commitSha ?: "a4f91bc"} • Duration: ${liveProd?.buildDurationSeconds ?: 22}s",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
                Button(
                    onClick = { showTriggerDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF002026)),
                    modifier = Modifier.testTag("trigger_deployment_btn")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Trigger Build", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        SectionHeader(title = "Deployment History & Logs", badgeText = "Audit-Verified")

        // Deployments List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(deployments, key = { it.id }) { dpl ->
                CyberCard(
                    modifier = Modifier.testTag("deployment_card_${dpl.id}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = dpl.projectName,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CyberCyanDark)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(text = dpl.environment.label, color = CyberCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = dpl.commitMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        StatusBadge(status = dpl.status.label)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SHA: ${dpl.commitSha} • Branch: ${dpl.branch}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                        Row {
                            OutlinedButton(
                                onClick = { selectedDeploymentForLogs = dpl },
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Build Logs", fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(dpl.deploymentUrl))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.OpenInBrowser, contentDescription = "View URL", tint = CyberCyan, modifier = Modifier.size(18.dp))
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

    // Trigger Deployment Dialog
    if (showTriggerDialog) {
        var branchName by remember { mutableStateOf("main") }
        var commitSha by remember { mutableStateOf("b92e71a") }
        var targetEnv by remember { mutableStateOf(EnvironmentType.PREVIEW) }

        AlertDialog(
            onDismissRequest = { showTriggerDialog = false },
            title = { Text("Trigger Vercel Build & Deploy", color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Builds are executed via the Vercel API and verified edge network.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    OutlinedTextField(value = branchName, onValueChange = { branchName = it }, label = { Text("Git Branch") }, singleLine = true)
                    OutlinedTextField(value = commitSha, onValueChange = { commitSha = it }, label = { Text("Commit SHA / Ref") }, singleLine = true)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { targetEnv = EnvironmentType.PREVIEW },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (targetEnv == EnvironmentType.PREVIEW) CyberCyanDark else CyberSurfaceElevated,
                                contentColor = if (targetEnv == EnvironmentType.PREVIEW) CyberCyan else TextSecondary
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Preview", fontSize = 12.sp)
                        }
                        Button(
                            onClick = { targetEnv = EnvironmentType.PRODUCTION },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (targetEnv == EnvironmentType.PRODUCTION) StatusGreen.copy(alpha = 0.2f) else CyberSurfaceElevated,
                                contentColor = if (targetEnv == EnvironmentType.PRODUCTION) StatusGreen else TextSecondary
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Production", fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.triggerDeployment(
                            projectId = projectId ?: "proj_mohitport",
                            projectName = "CyberMohit AI Portfolio",
                            env = targetEnv,
                            commitSha = commitSha,
                            branch = branchName
                        )
                        showTriggerDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF002026))
                ) {
                    Text("Start Build", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTriggerDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = CyberSurfaceElevated
        )
    }

    // Build Logs Dialog
    selectedDeploymentForLogs?.let { dpl ->
        AlertDialog(
            onDismissRequest = { selectedDeploymentForLogs = null },
            title = {
                Text(
                    text = "Vercel Build Logs (${dpl.commitSha})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
            },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF050811))
                        .padding(10.dp)
                ) {
                    LazyColumn {
                        items(dpl.logs) { line ->
                            Text(
                                text = line,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = if (line.contains("Completed") || line.contains("success", ignoreCase = true)) StatusGreen else Color(0xFFCBD5E1)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedDeploymentForLogs = null }) {
                    Text("Close", color = CyberCyan)
                }
            },
            containerColor = CyberSurfaceElevated
        )
    }
}
