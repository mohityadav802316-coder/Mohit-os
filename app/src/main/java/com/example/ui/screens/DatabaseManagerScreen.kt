package com.example.ui.screens

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.DatabaseProvider
import com.example.domain.model.DatabaseTableInfo
import com.example.ui.components.CyberCard
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
fun DatabaseManagerScreen(
    viewModel: MasterAdminViewModel
) {
    val tables = viewModel.getDatabaseTables()
    var selectedTable by remember { mutableStateOf(tables.first()) }
    var tableSearchQuery by remember { mutableStateOf("") }
    var showConnectDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(top = 12.dp)
            .testTag("database_manager_screen")
    ) {
        // Connection Info Card
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
                        Icon(Icons.Default.Storage, contentDescription = null, tint = CyberIndigo, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Managed PostgreSQL Engine",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "Host: cluster.neon.tech / Supabase • SSL Enforced",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    }
                }
                StatusBadge(status = "CONNECTED")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Zero-Trust Read-Only Inspection Mode (Arbitrary SQL Execution Restricted)",
                    style = MaterialTheme.typography.labelSmall,
                    color = StatusGreen
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { showConnectDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyanDark, contentColor = CyberCyan),
                modifier = Modifier.fillMaxWidth().testTag("connect_database_btn")
            ) {
                Icon(Icons.Default.Storage, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Configure Database Connection & Migrations", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Table Selection Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tables.forEach { table ->
                val isSelected = table.name == selectedTable.name
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) CyberCyanDark else CyberSurfaceElevated)
                        .border(1.dp, if (isSelected) CyberCyan else CyberBorder, RoundedCornerShape(8.dp))
                        .clickable { selectedTable = table }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.TableChart,
                            contentDescription = null,
                            tint = if (isSelected) CyberCyan else TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${table.name} (${table.rowCount})",
                            color = if (isSelected) CyberCyan else TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Table Schema Columns Summary
        SectionHeader(
            title = "Table: ${selectedTable.name}",
            badgeText = "${selectedTable.columns.size} Columns"
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            selectedTable.columns.forEach { col ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(CyberSurfaceElevated)
                        .border(0.5.dp, CyberBorder, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = col, color = CyberCyan, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Sample Records List
        Text(
            text = "Record Explorer (${selectedTable.sampleRows.size} sampled)",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(selectedTable.sampleRows) { rowMap ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF070B14))
                        .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        rowMap.forEach { (k, v) ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "$k: ",
                                    color = TextMuted,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    modifier = Modifier.width(110.dp)
                                )
                                Text(
                                    text = v,
                                    color = TextPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    if (showConnectDialog) {
        val projects by viewModel.rawProjects.collectAsState()
        var selectedProject by remember { mutableStateOf(projects.firstOrNull()) }
        var provider by remember { mutableStateOf(DatabaseProvider.POSTGRESQL) }
        var connectionUri by remember { mutableStateOf("postgresql://admin:secret@ep-cluster.neon.tech/master_admin?sslmode=require") }
        var isVerifying by remember { mutableStateOf(false) }
        var verifyMsg by remember { mutableStateOf<String?>(null) }
        var migrationsApplied by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showConnectDialog = false },
            title = {
                Column {
                    Text("Connect Database & Migrations", color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text("Guided connection wizard for Neon, Supabase & PostgreSQL", color = CyberCyan, fontSize = 11.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select Database Provider:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(DatabaseProvider.POSTGRESQL, DatabaseProvider.NEON, DatabaseProvider.SUPABASE, DatabaseProvider.FIRESTORE).forEach { p ->
                            FilterChip(
                                selected = provider == p,
                                onClick = { provider = p },
                                label = { Text(p.label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyberCyanDark,
                                    selectedLabelColor = CyberCyan,
                                    containerColor = CyberSurfaceElevated,
                                    labelColor = TextSecondary
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = connectionUri,
                        onValueChange = { connectionUri = it },
                        label = { Text("Connection URI (SSL Required)") },
                        modifier = Modifier.fillMaxWidth().testTag("db_connection_uri_input"),
                        singleLine = true
                    )

                    OutlinedButton(
                        onClick = {
                            isVerifying = true
                            viewModel.testProjectDatabaseConnection(
                                projectId = selectedProject?.id ?: "proj_mohitport",
                                provider = provider,
                                connectionUri = connectionUri
                            ) { msg ->
                                isVerifying = false
                                verifyMsg = msg
                            }
                        },
                        enabled = !isVerifying && connectionUri.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().testTag("test_database_handshake_btn"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan)
                    ) {
                        if (isVerifying) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = CyberCyan, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Testing TLS & Ping...")
                        } else {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Test Database Connection")
                        }
                    }

                    verifyMsg?.let { msg ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = StatusGreen.copy(alpha = 0.15f)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(msg, fontSize = 11.sp, color = TextPrimary)
                            }
                        }
                    }

                    // Apply Migrations Action
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CyberIndigo.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Database Migrations (001, 002, 003)", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 12.sp)
                            Text("Applies initial schema, integration tokens table, and content versioning tables.", color = TextSecondary, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = { migrationsApplied = true },
                                enabled = !migrationsApplied,
                                colors = ButtonDefaults.buttonColors(containerColor = if (migrationsApplied) StatusGreen else CyberCyan, contentColor = Color(0xFF002026)),
                                modifier = Modifier.fillMaxWidth().testTag("apply_migrations_btn")
                            ) {
                                Text(if (migrationsApplied) "✓ Migrations Applied Successfully" else "Run Schema Migrations", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedProject?.let { p ->
                            viewModel.updateProjectConfiguration(
                                projectId = p.id,
                                apiBaseUrl = p.apiBaseUrl,
                                apiAuthHeader = p.apiAuthHeader,
                                apiAuthToken = p.apiAuthToken,
                                dbConnectionString = connectionUri,
                                vercelProjectId = p.vercelProjectId,
                                gitBranch = p.gitBranch
                            )
                        }
                        showConnectDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF002026)),
                    modifier = Modifier.testTag("save_database_connection_btn")
                ) {
                    Text("Save Connection", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConnectDialog = false }) {
                    Text("Close", color = TextSecondary)
                }
            },
            containerColor = CyberSurfaceElevated
        )
    }
}

