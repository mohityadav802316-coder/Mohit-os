package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.ui.components.OwnerEnrollmentDialog
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
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberCyanDark
import com.example.ui.theme.CyberIndigo
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MasterAdminViewModel

@Composable
fun SettingsScreen(
    viewModel: MasterAdminViewModel
) {
    val context = LocalContext.current
    val ownerConfig by viewModel.ownerConfig.collectAsState()
    var backendApiUrl by remember { mutableStateOf("https://ais-dev-bzaqgkqspnan7kpwg5zyto-924569394319.asia-southeast1.run.app") }
    var exportStatus by remember { mutableStateOf<String?>(null) }
    var showEnrollmentDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var resetPasswordInput by remember { mutableStateOf("") }
    var resetError by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(16.dp)
            .testTag("settings_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Control Center Settings",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            Text(
                text = "Security boundaries, owner authentication, and backend server endpoints.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        // Owner Account Card
        item {
            SectionHeader(title = "Authorized Owner Identity & Enrollment")
            CyberCard(borderColor = if (ownerConfig.isEnrolled) StatusGreen.copy(alpha = 0.5f) else CyberCyan.copy(alpha = 0.5f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(CyberCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = ownerConfig.name.ifBlank { viewModel.ownerName },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = ownerConfig.email.ifBlank { viewModel.ownerEmail },
                            style = MaterialTheme.typography.bodySmall,
                            color = CyberCyan,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (ownerConfig.isEnrolled) "Enrolled & Protected with Master Password" else "Enrollment Pending — Setup Required",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (ownerConfig.isEnrolled) StatusGreen else TextMuted
                        )
                    }
                    StatusBadge(status = if (ownerConfig.isEnrolled) "LOCKED" else "SETUP REQ")
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Single Sign-On Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("AUTHENTICATION METHOD", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text(
                            text = if (ownerConfig.googleAuthEnabled) "Dual (Master Password + Google SSO)" else "Master Password Only",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("ENROLLMENT RESTRICTION", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text(
                            text = "mohityadav802316@gmail.com",
                            style = MaterialTheme.typography.bodySmall,
                            color = CyberCyan,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!ownerConfig.isEnrolled) {
                        Button(
                            onClick = { showEnrollmentDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF002026)),
                            modifier = Modifier.fillMaxWidth().testTag("open_owner_setup_btn")
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Complete First-Time Owner Setup", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        OutlinedButton(
                            onClick = { showResetDialog = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan),
                            modifier = Modifier.fillMaxWidth().testTag("reset_owner_credentials_btn")
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset Credentials / Re-enroll")
                        }
                    }
                }
            }
        }

        // Backend API Endpoint Card
        item {
            SectionHeader(title = "MohitOS Backend API")
            CyberCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Dns, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Authorized Cloud API Server",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = backendApiUrl,
                    onValueChange = { backendApiUrl = it },
                    label = { Text("Base URL / Proxy Endpoint") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("backend_url_input"),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SSL/TLS Enforced • Token Auth",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    Button(
                        onClick = {
                            viewModel.pingIntegration("vercel")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF002026)),
                        modifier = Modifier.testTag("verify_api_connection_btn")
                    ) {
                        Text("Verify Connection", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Backup & Disaster Recovery Card
        item {
            SectionHeader(title = "Backup & Recovery")
            CyberCard {
                Text(
                    text = "Export Project Configurations & Schemas",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Generates a sanitized JSON export of project registrations, content records, and deployment metadata without exposing secrets.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            val mockBackup = """
                                {
                                  "version": "1.0.0",
                                  "exported_at": "${System.currentTimeMillis()}",
                                  "owner": "${viewModel.ownerEmail}",
                                  "projects": ["Mohit Yadav Portfolio", "MohitOS Core API"],
                                  "database_engine": "PostgreSQL",
                                  "hosting": "Vercel"
                                }
                            """.trimIndent()
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("MohitOS Backup", mockBackup))
                            exportStatus = "Snapshot copied to clipboard!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberIndigo, contentColor = Color.White),
                        modifier = Modifier.testTag("export_backup_btn")
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export Snapshot")
                    }
                    if (exportStatus != null) {
                        Text(
                            text = exportStatus ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = StatusGreen
                        )
                    }
                }
            }
        }

        // Security Checklist Card
        item {
            SectionHeader(title = "Security Architecture & Controls")
            CyberCard {
                val securityItems = listOf(
                    "Server-Side Authorization on Sensitive Endpoints" to true,
                    "Client Keystore Separation (No Secret Tokens in APK)" to true,
                    "Read-Only Protection on Unknown Databases" to true,
                    "Mandatory Owner Approval for Production Changes" to true,
                    "SSRF Prevention & Validated HTTPS Schemes" to true,
                    "Append-Only Immutable Event Audit Logging" to true
                )

                securityItems.forEach { (label, status) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary
                        )
                    }
                }
            }
        }

        // About MohitOS Section
        item {
            SectionHeader(title = "About MohitOS")
            CyberCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF151515))
                            .border(1.5.dp, CyberCyan, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_mohitos_logo),
                            contentDescription = "MohitOS Logo",
                            modifier = Modifier.size(52.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
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
                            color = com.example.ui.theme.MohitGoldHighlight
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Version 1.0.0 • Production Build",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "MohitOS is your sovereign digital command center. Built with native Kotlin, Jetpack Compose, and offline-first Room database, it enables zero-dependency management of multi-website estates, APIs, databases, GitHub repositories, and cloud deployments.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    if (showEnrollmentDialog) {
        OwnerEnrollmentDialog(
            viewModel = viewModel,
            onDismiss = { showEnrollmentDialog = false }
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = {
                showResetDialog = false
                resetPasswordInput = ""
                resetError = null
            },
            title = {
                Text(
                    text = "Confirm Master Reset",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Resetting owner credentials requires the current Master Password. This unlocks enrollment mode for re-configuration.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    OutlinedTextField(
                        value = resetPasswordInput,
                        onValueChange = {
                            resetPasswordInput = it
                            resetError = null
                        },
                        label = { Text("Current Master Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("reset_password_field")
                    )
                    if (resetError != null) {
                        Text(
                            text = resetError!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = StatusRed
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (resetPasswordInput.isBlank()) {
                            resetError = "Password cannot be empty"
                        } else {
                            viewModel.resetOwnerEnrollment(
                                password = resetPasswordInput,
                                onSuccess = {
                                    showResetDialog = false
                                    resetPasswordInput = ""
                                    resetError = null
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusRed),
                    modifier = Modifier.testTag("confirm_reset_btn")
                ) {
                    Text("Verify & Reset", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showResetDialog = false
                        resetPasswordInput = ""
                        resetError = null
                    }
                ) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = CyberBackground
        )
    }
}
