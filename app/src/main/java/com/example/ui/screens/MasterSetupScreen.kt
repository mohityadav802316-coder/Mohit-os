package com.example.ui.screens

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.IntegrationProvider
import com.example.domain.model.IntegrationService
import com.example.domain.model.IntegrationState
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
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MasterAdminViewModel

@Composable
fun MasterSetupScreen(
    viewModel: MasterAdminViewModel
) {
    val services by viewModel.integrationServices.collectAsState()
    var selectedServiceForConfig by remember { mutableStateOf<IntegrationService?>(null) }

    val activeCount = services.count { it.state == IntegrationState.ACTIVE }
    val verifiedCount = services.count { it.state == IntegrationState.VERIFIED || it.state == IntegrationState.ACTIVE }
    val pendingConfigCount = services.count { it.state == IntegrationState.NOT_CONFIGURED || it.state == IntegrationState.CONFIG_REQUIRED }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(16.dp)
            .testTag("master_setup_screen")
    ) {
        // Master Setup Header Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CyberSurfaceElevated)
                .border(1.dp, CyberCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
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
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberCyanDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "MohitOS • SETUP CENTER",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                color = CyberCyan,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Service Configuration & Activation Wizard",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Configure external cloud services individually after installation. Test connections, audit permissions, and activate when ready. No unverified services are shown as active.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Readiness Summary Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                title = "SERVICES",
                value = "${services.size}",
                subtitle = "Total Connectors",
                icon = Icons.Default.Extension,
                iconTint = CyberCyan,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "VERIFIED",
                value = "$verifiedCount",
                subtitle = "Connection Tested",
                icon = Icons.Default.CheckCircle,
                iconTint = StatusGreen,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "ACTIVE",
                value = "$activeCount",
                subtitle = "Live Integrations",
                icon = Icons.Default.CloudDone,
                iconTint = if (activeCount > 0) StatusGreen else TextMuted,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        SectionHeader(
            title = "Integrations Registry",
            badgeText = "$pendingConfigCount Awaiting Config"
        )

        // Services List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(services, key = { it.id }) { service ->
                IntegrationServiceCard(
                    service = service,
                    onConfigure = { selectedServiceForConfig = service }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Comprehensive Service Configuration Dialog / Wizard
    selectedServiceForConfig?.let { currentService ->
        // Retrieve latest state from repository flow
        val liveService = services.firstOrNull { it.id == currentService.id } ?: currentService

        IntegrationSetupDialog(
            service = liveService,
            onDismiss = { selectedServiceForConfig = null },
            onUpdateField = { key, value ->
                viewModel.updateIntegrationField(liveService.id, key, value)
            },
            onTestConnection = {
                viewModel.testAndVerifyIntegration(liveService.id)
            },
            onToggleActive = { activate ->
                viewModel.toggleIntegrationActive(liveService.id, activate)
            },
            onReset = {
                viewModel.resetIntegration(liveService.id)
            }
        )
    }
}

@Composable
fun IntegrationServiceCard(
    service: IntegrationService,
    onConfigure: () -> Unit
) {
    val icon = when (service.provider) {
        IntegrationProvider.GOOGLE_AUTH -> Icons.Default.Security
        IntegrationProvider.EMAIL_AUTH -> Icons.Default.Lock
        IntegrationProvider.VERCEL -> Icons.Default.Cloud
        IntegrationProvider.GITHUB -> Icons.Default.Code
        IntegrationProvider.POSTGRESQL -> Icons.Default.Storage
        IntegrationProvider.SUPABASE -> Icons.Default.Dns
        IntegrationProvider.FIRESTORE -> Icons.Default.Storage
        IntegrationProvider.STORAGE -> Icons.Default.Storage
        IntegrationProvider.CUSTOM_REST -> Icons.Default.Terminal
        IntegrationProvider.ANALYTICS -> Icons.Default.NetworkCheck
    }

    CyberCard(
        modifier = Modifier.testTag("setup_card_${service.id}"),
        onClick = onConfigure,
        borderColor = when (service.state) {
            IntegrationState.ACTIVE -> StatusGreen.copy(alpha = 0.6f)
            IntegrationState.VERIFIED -> CyberCyan.copy(alpha = 0.6f)
            IntegrationState.CONNECTION_FAILED -> StatusRed.copy(alpha = 0.6f)
            else -> CyberBorder
        }
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
                        .background(CyberCyanDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = service.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = "${service.category} • ${service.fields.size} Config Fields",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
            StatusBadge(status = service.state.label)
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = service.purpose,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Requires: ${service.requiredCredentials.joinToString(", ")}",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = onConfigure,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (service.state == IntegrationState.ACTIVE) StatusGreen else CyberCyan,
                    contentColor = Color(0xFF002026)
                ),
                modifier = Modifier
                    .height(32.dp)
                    .testTag("configure_btn_${service.id}")
            ) {
                Text(
                    text = if (service.state == IntegrationState.ACTIVE) "Manage" else "Configure",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun IntegrationSetupDialog(
    service: IntegrationService,
    onDismiss: () -> Unit,
    onUpdateField: (String, String) -> Unit,
    onTestConnection: () -> Unit,
    onToggleActive: (Boolean) -> Unit,
    onReset: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = service.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = service.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = CyberCyan
                    )
                }
                StatusBadge(status = service.state.label)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Purpose section
                item {
                    Text(
                        text = "PURPOSE & SCOPE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextMuted,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = service.purpose,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                // Step-by-Step Instructions
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "HOW TO OBTAIN CREDENTIALS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = CyberCyan,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF070B14))
                            .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            service.instructions.forEach { step ->
                                Text(
                                    text = step,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                            }
                        }
                    }
                }

                // Configuration Fields
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "CONFIGURATION CREDENTIALS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextMuted,
                        fontFamily = FontFamily.Monospace
                    )
                }

                items(service.fields) { field ->
                    var isPasswordVisible by remember { mutableStateOf(!field.isSecret) }
                    Column {
                        OutlinedTextField(
                            value = field.value,
                            onValueChange = { onUpdateField(field.key, it) },
                            label = { Text(field.label) },
                            placeholder = { Text(field.placeholder, color = TextMuted) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("setup_field_${field.key}"),
                            singleLine = true,
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                if (field.isSecret) {
                                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                        Icon(
                                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Toggle Secret",
                                            tint = TextSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            },
                            textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                        )
                        if (field.description.isNotBlank()) {
                            Text(
                                text = field.description,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                            )
                        }
                    }
                }

                // Connection Test Action Button
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = onTestConnection,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF002026)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("test_connection_btn")
                    ) {
                        Icon(Icons.Default.NetworkCheck, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Test Connection & Verify Permissions", fontWeight = FontWeight.Bold)
                    }
                }

                // Diagnostics Output Console
                if (service.diagnosticLog.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "DIAGNOSTIC VERIFICATION LOG",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (service.state == IntegrationState.CONNECTION_FAILED) StatusRed else StatusGreen,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF050811))
                                .border(1.dp, if (service.state == IntegrationState.CONNECTION_FAILED) StatusRed else CyberBorder, RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Column {
                                service.diagnosticLog.forEach { logLine ->
                                    Text(
                                        text = logLine,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = if (logLine.contains("ERR") || logLine.contains("failed", ignoreCase = true)) StatusRed else StatusGreen
                                    )
                                }
                            }
                        }
                    }
                }

                // Error Diagnostics Panel
                if (service.errorDetails != null) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(StatusRed.copy(alpha = 0.15f))
                                .border(1.dp, StatusRed.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(Icons.Default.Error, contentDescription = null, tint = StatusRed, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = service.errorDetails,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFFECDD3),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // Activation Controls
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberSurfaceElevated)
                            .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Activate Integration",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (service.state == IntegrationState.VERIFIED || service.state == IntegrationState.ACTIVE)
                                        "Enable service for real-time operations"
                                    else
                                        "Requires successful test verification first",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                            Switch(
                                checked = service.isActive,
                                onCheckedChange = { onToggleActive(it) },
                                enabled = service.state == IntegrationState.VERIFIED || service.state == IntegrationState.ACTIVE,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF002026),
                                    checkedTrackColor = StatusGreen,
                                    uncheckedThumbColor = TextMuted,
                                    uncheckedTrackColor = Color(0xFF1E293B)
                                ),
                                modifier = Modifier.testTag("activate_switch_${service.id}")
                            )
                        }
                    }
                }

                // Reset Action
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onReset,
                            colors = ButtonDefaults.textButtonColors(contentColor = TextMuted)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reset Credentials to Default", fontSize = 11.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyanDark, contentColor = CyberCyan)
            ) {
                Text("Close")
            }
        },
        containerColor = CyberSurfaceElevated
    )
}
