package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun OwnerEnrollmentDialog(
    viewModel: MasterAdminViewModel,
    onDismiss: () -> Unit = {}
) {
    var ownerName by remember { mutableStateOf("Mohit Yadav") }
    var ownerEmail by remember { mutableStateOf("mohityadav802316@gmail.com") }
    var masterPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var bootstrapCode by remember { mutableStateOf("MASTER_BOOTSTRAP_2026") }
    var enableGoogleAuth by remember { mutableStateOf(true) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { /* Modal enforces owner enrollment setup */ },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(CyberCyan.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "MohitOS • Owner Setup",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = "YOUR DIGITAL CONTROL CENTER",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp),
                        color = CyberCyan
                    )
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("owner_enrollment_dialog"),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    // Security Notice Banner
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CyberIndigo.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CyberIndigo.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "RESTRICTED ACCESS: Administrator enrollment is cryptographically locked to Mohit Yadav. Public user registration is permanently blocked.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = TextSecondary
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = ownerName,
                        onValueChange = { ownerName = it },
                        label = { Text("Owner Full Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("owner_name_input"),
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = CyberCyan) },
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
                        value = ownerEmail,
                        onValueChange = { ownerEmail = it },
                        label = { Text("Owner Email Address") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("owner_email_input"),
                        leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = CyberCyan) },
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
                        value = masterPassword,
                        onValueChange = { masterPassword = it },
                        label = { Text("MohitOS Master Password (Min. 6 chars)") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("owner_password_input"),
                        leadingIcon = { Icon(Icons.Default.Fingerprint, contentDescription = null, tint = CyberCyan) },
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
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Master Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("owner_confirm_password_input"),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = CyberCyan) },
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
                        value = bootstrapCode,
                        onValueChange = { bootstrapCode = it },
                        label = { Text("Secure Bootstrap Enrollment Secret") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("owner_bootstrap_code_input"),
                        leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, tint = CyberCyan) },
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Configure Google Sign-In",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Text(
                                text = "Enable Single Sign-On for authorized owner email",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        Switch(
                            checked = enableGoogleAuth,
                            onCheckedChange = { enableGoogleAuth = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF002026),
                                checkedTrackColor = CyberCyan
                            )
                        )
                    }
                }

                if (errorMessage != null) {
                    item {
                        Text(
                            text = errorMessage!!,
                            color = StatusRed,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (masterPassword.length < 6) {
                        errorMessage = "Password must be at least 6 characters."
                        return@Button
                    }
                    if (masterPassword != confirmPassword) {
                        errorMessage = "Passwords do not match."
                        return@Button
                    }
                    if (!ownerEmail.trim().equals("mohityadav802316@gmail.com", ignoreCase = true)) {
                        errorMessage = "Unauthorized email: Must be mohityadav802316@gmail.com"
                        return@Button
                    }
                    if (bootstrapCode.trim() != "MASTER_BOOTSTRAP_2026") {
                        errorMessage = "Invalid Bootstrap Code. Expected MASTER_BOOTSTRAP_2026"
                        return@Button
                    }

                    errorMessage = null
                    isSubmitting = true
                    viewModel.enrollOwner(
                        name = ownerName,
                        email = ownerEmail,
                        password = masterPassword,
                        bootstrapCode = bootstrapCode,
                        enableGoogle = enableGoogleAuth,
                        onSuccess = {
                            isSubmitting = false
                            onDismiss()
                        }
                    )
                },
                enabled = !isSubmitting && masterPassword.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberCyan,
                    contentColor = Color(0xFF002026)
                ),
                modifier = Modifier.testTag("submit_owner_enrollment_btn")
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Complete & Lock Enrollment", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = CyberSurfaceElevated
    )
}
