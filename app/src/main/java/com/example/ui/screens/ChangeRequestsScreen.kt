package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Approval
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.domain.model.ChangeRequest
import com.example.domain.model.ChangeStatus
import com.example.ui.components.CyberCard
import com.example.ui.components.DiffViewer
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberCyanDark
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MasterAdminViewModel

@Composable
fun ChangeRequestsScreen(
    viewModel: MasterAdminViewModel
) {
    val allRequests by viewModel.changeRequests.collectAsState()
    var statusFilter by remember { mutableStateOf<ChangeStatus?>(null) }
    var requestToReject by remember { mutableStateOf<ChangeRequest?>(null) }

    val filteredRequests = if (statusFilter == null) {
        allRequests
    } else {
        allRequests.filter { it.status == statusFilter }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(16.dp)
            .testTag("change_requests_screen")
    ) {
        Text(
            text = "Approval & Change Center",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )
        Text(
            text = "Review, validate diffs, and authorize production mutations.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Status Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = statusFilter == null,
                onClick = { statusFilter = null },
                label = { Text("All (${allRequests.size})") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CyberCyanDark,
                    selectedLabelColor = CyberCyan,
                    containerColor = CyberSurfaceElevated,
                    labelColor = TextSecondary
                )
            )
            ChangeStatus.values().forEach { status ->
                FilterChip(
                    selected = statusFilter == status,
                    onClick = {
                        statusFilter = if (statusFilter == status) null else status
                    },
                    label = { Text(status.label) },
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

        // Requests List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(filteredRequests, key = { it.id }) { req ->
                CyberCard(
                    modifier = Modifier.testTag("change_request_card_${req.id}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = req.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Text(
                                text = "${req.projectName} • ${req.requestType.label}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        StatusBadge(status = req.status.label)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Reason: ${req.reason}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberCyanDark)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "RISK: ${req.riskLevel.label.uppercase()}",
                                color = when (req.riskLevel.name) {
                                    "CRITICAL", "HIGH" -> StatusRed
                                    "MEDIUM" -> com.example.ui.theme.StatusAmber
                                    else -> CyberCyan
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberSurfaceElevated)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Target: ${req.targetEnvironment.label}",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Before vs After Diff
                    if (req.beforeValue.isNotBlank() || req.afterValue.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        DiffViewer(beforeText = req.beforeValue, afterText = req.afterValue)
                    }

                    if (req.status == ChangeStatus.PENDING) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.approveChangeRequest(req.id, "Approved by Owner")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = StatusGreen, contentColor = Color.White),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("approve_btn_${req.id}")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Approve & Execute", fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { requestToReject = req },
                                colors = ButtonDefaults.buttonColors(containerColor = StatusRed.copy(alpha = 0.2f), contentColor = StatusRed),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("reject_btn_${req.id}")
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Reject")
                            }
                        }
                    } else if (req.reviewerNotes != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Notes: ${req.reviewerNotes}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Reject Dialog
    requestToReject?.let { req ->
        var rejectReason by remember { mutableStateOf("Does not meet production criteria") }

        AlertDialog(
            onDismissRequest = { requestToReject = null },
            title = { Text("Reject Change Request?", color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Provide a rejection reason for the audit trail:", color = TextSecondary)
                    OutlinedTextField(
                        value = rejectReason,
                        onValueChange = { rejectReason = it },
                        label = { Text("Rejection Reason") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.rejectChangeRequest(req.id, rejectReason)
                        requestToReject = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusRed)
                ) {
                    Text("Confirm Rejection")
                }
            },
            dismissButton = {
                TextButton(onClick = { requestToReject = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = CyberSurfaceElevated
        )
    }
}
