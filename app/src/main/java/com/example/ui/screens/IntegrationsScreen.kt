package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.NetworkPing
import androidx.compose.material.icons.filled.Settings
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
import com.example.domain.model.IntegrationService
import com.example.domain.model.IntegrationState
import com.example.ui.components.CyberCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberCyanDark
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MasterAdminViewModel

@Composable
fun IntegrationsScreen(
    viewModel: MasterAdminViewModel,
    projectId: String? = null
) {
    val services by viewModel.integrationServices.collectAsState()
    var selectedServiceForConfig by remember { mutableStateOf<IntegrationService?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(top = 12.dp)
            .testTag("integrations_screen")
    ) {
        Text(
            text = "API & Service Integrations Hub",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )
        Text(
            text = "Connected hosting providers, source control, databases, and custom REST connectors.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(services, key = { it.id }) { service ->
                CyberCard(
                    modifier = Modifier.testTag("integration_card_${service.id}")
                ) {
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
                                    .background(CyberCyan.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Extension, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = service.name,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${service.category} • ${service.fields.size} Fields",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    fontFamily = FontFamily.Monospace,
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
                        color = TextSecondary,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (service.latencyMs > 0) "Latency: ${service.latencyMs}ms • Active: ${service.isActive}" else "State: ${service.state.label}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (service.latencyMs > 0) StatusGreen else TextMuted,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                        Row {
                            OutlinedButton(
                                onClick = { viewModel.testAndVerifyIntegration(service.id) },
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(Icons.Default.NetworkPing, contentDescription = null, modifier = Modifier.size(14.dp), tint = CyberCyan)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Test Ping", fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = { selectedServiceForConfig = service },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (service.state == IntegrationState.ACTIVE) StatusGreen else CyberCyan,
                                    contentColor = Color(0xFF002026)
                                ),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(if (service.state == IntegrationState.ACTIVE) "Manage" else "Configure", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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

    // Modal Configuration Sheet
    selectedServiceForConfig?.let { currentService ->
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
