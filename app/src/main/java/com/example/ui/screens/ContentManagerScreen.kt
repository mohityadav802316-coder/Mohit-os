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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.domain.model.HeroContent
import com.example.domain.model.PortfolioProjectItem
import com.example.ui.components.CyberCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
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
fun ContentManagerScreen(
    viewModel: MasterAdminViewModel,
    projectId: String
) {
    val contentState by viewModel.websiteContent.collectAsState()
    val dynamicFields by viewModel.getDynamicFieldsForProject(projectId).collectAsState(initial = emptyList())
    var selectedSectionTab by remember { mutableIntStateOf(0) } // 0: Hero, 1: Skills, 2: Portfolio Projects, 3: Custom CMS Schema, 4: Preview

    var showAddProjectDialog by remember { mutableStateOf(false) }
    var showAddFieldDialog by remember { mutableStateOf(false) }

    // Editable Hero States
    var heroTitle by remember(contentState.hero.title) { mutableStateOf(contentState.hero.title) }
    var heroSubtitle by remember(contentState.hero.subtitle) { mutableStateOf(contentState.hero.subtitle) }
    var heroBadge by remember(contentState.hero.badge) { mutableStateOf(contentState.hero.badge) }
    var heroBio by remember(contentState.hero.bio) { mutableStateOf(contentState.hero.bio) }
    var heroAvatar by remember(contentState.hero.avatarUrl) { mutableStateOf(contentState.hero.avatarUrl) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 12.dp)
            .testTag("content_manager_screen")
    ) {
        // Sync & Publishing Status Bar
        CyberCard(
            borderColor = when (contentState.syncStatus.label) {
                "Live on Website" -> StatusGreen.copy(alpha = 0.5f)
                else -> CyberCyan.copy(alpha = 0.5f)
            }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "WEBSITE DATA PIPELINE",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        StatusBadge(status = contentState.syncStatus.label)
                    }
                    Text(
                        text = "Connected: CyberMohit AI Portfolio Data Source",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            viewModel.publishWebsiteContent(projectId)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusGreen, contentColor = Color.White),
                        modifier = Modifier.testTag("publish_website_btn")
                    ) {
                        Icon(Icons.Default.Publish, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Publish Live", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Section Tabs
        TabRow(
            selectedTabIndex = selectedSectionTab,
            containerColor = CyberSurfaceElevated,
            contentColor = CyberCyan,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedSectionTab]),
                    color = CyberCyan
                )
            }
        ) {
            listOf("Hero Section", "Skills Matrix", "Projects", "CMS Schema", "Live Preview").forEachIndexed { index, title ->
                Tab(
                    selected = selectedSectionTab == index,
                    onClick = { selectedSectionTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedSectionTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedSectionTab == index) CyberCyan else TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Content
        when (selectedSectionTab) {
            0 -> {
                // Hero Section Editor
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = heroTitle,
                            onValueChange = { heroTitle = it },
                            label = { Text("Display Name / Main Header") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("hero_title_input"),
                            singleLine = true
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = heroSubtitle,
                            onValueChange = { heroSubtitle = it },
                            label = { Text("Professional Subtitle / Role Tagline") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("hero_subtitle_input"),
                            singleLine = true
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = heroBadge,
                            onValueChange = { heroBadge = it },
                            label = { Text("Status Badge (e.g. Available for Projects)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = heroBio,
                            onValueChange = { heroBio = it },
                            label = { Text("Executive Biography / Intro") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = heroAvatar,
                            onValueChange = { heroAvatar = it },
                            label = { Text("Avatar / Profile Image URL") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        Button(
                            onClick = {
                                val updated = HeroContent(
                                    title = heroTitle,
                                    subtitle = heroSubtitle,
                                    badge = heroBadge,
                                    bio = heroBio,
                                    avatarUrl = heroAvatar,
                                    ctaText = contentState.hero.ctaText,
                                    ctaUrl = contentState.hero.ctaUrl
                                )
                                viewModel.updateHeroContent(updated)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF002026)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("save_hero_btn")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save Hero Section to Backend", fontWeight = FontWeight.Bold)
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
            1 -> {
                // Skills Matrix Editor
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "Adjust proficiency or add technologies to the portfolio skills matrix.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    items(contentState.skills, key = { it.id }) { skill ->
                        var currentSlider by remember(skill.proficiency) { mutableFloatStateOf(skill.proficiency.toFloat()) }
                        CyberCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = skill.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Category: ${skill.category}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                                Text(
                                    text = "${currentSlider.toInt()}%",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = CyberCyan
                                )
                            }
                            Slider(
                                value = currentSlider,
                                onValueChange = { currentSlider = it },
                                onValueChangeFinished = {
                                    viewModel.updateSkillProficiency(skill.id, currentSlider.toInt())
                                },
                                valueRange = 0f..100f,
                                colors = SliderDefaults.colors(
                                    thumbColor = CyberCyan,
                                    activeTrackColor = CyberCyan,
                                    inactiveTrackColor = CyberBorder
                                )
                            )
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
            2 -> {
                // Projects Showcase Editor
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Featured Portfolio Projects",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Button(
                                onClick = { showAddProjectDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberCyanDark, contentColor = CyberCyan)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Project Card", fontSize = 12.sp)
                            }
                        }
                    }
                    items(contentState.projects, key = { it.id }) { projItem ->
                        CyberCard {
                            Text(
                                text = projItem.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = projItem.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                projItem.tags.forEach { tag ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(CyberCyanDark)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = tag, color = CyberCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
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
            3 -> {
                // Dynamic CMS Schema & Field Configurator
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Custom CMS Schema & Fields",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Add and configure arbitrary website content fields without source code changes.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                            Button(
                                onClick = { showAddFieldDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF002026)),
                                modifier = Modifier.testTag("add_custom_field_btn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Field", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }

                    if (dynamicFields.isEmpty()) {
                        item {
                            CyberCard {
                                Text(
                                    text = "No custom fields configured for this project yet. Tap '+ Add Field' to declare custom content sections like announcements, testimonials, pricing, or custom text.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }
                        }
                    } else {
                        items(dynamicFields, key = { it.id }) { field ->
                            var fieldValue by remember(field.fieldValue) { mutableStateOf(field.fieldValue) }
                            CyberCard(borderColor = CyberBorder) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = field.fieldLabel,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "Key: ${field.sectionKey}.${field.fieldKey} • Type: ${field.fieldType}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = CyberCyan,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteDynamicField(field.id) }
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete field", tint = TextMuted, modifier = Modifier.size(18.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = fieldValue,
                                    onValueChange = { fieldValue = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text("Field Value (${field.fieldType})") },
                                    trailingIcon = {
                                        if (fieldValue != field.fieldValue) {
                                            IconButton(
                                                onClick = { viewModel.updateDynamicFieldValue(field.id, fieldValue) }
                                            ) {
                                                Icon(Icons.Default.Save, contentDescription = "Save value", tint = StatusGreen)
                                            }
                                        }
                                    },
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
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
            4 -> {
                // Live Website Preview
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Text(
                            text = "Live Website Rendering Preview (CyberMohit AI Portfolio)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "Accurate representation of rendered DOM components on Vercel.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    item {
                        // Simulated Website Hero Container
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF030712))
                                .border(1.dp, CyberBorder, RoundedCornerShape(16.dp))
                                .padding(20.dp)
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(CyberCyan.copy(alpha = 0.15f))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "● ${contentState.hero.badge}",
                                        color = CyberCyan,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = contentState.hero.title,
                                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = contentState.hero.subtitle,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Color(0xFF38BDF8)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = contentState.hero.bio,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF94A3B8)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(CyberCyan)
                                            .padding(horizontal = 14.dp, vertical = 8.dp)
                                    ) {
                                        Text(text = "View Projects", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 14.dp, vertical = 8.dp)
                                    ) {
                                        Text(text = "Contact", color = Color.White, fontSize = 12.sp)
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
    }

    // Add Portfolio Project Dialog
    if (showAddProjectDialog) {
        var title by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }
        var tagsStr by remember { mutableStateOf("Kotlin, Compose, Cloud") }
        var liveUrl by remember { mutableStateOf("https://") }
        var ghUrl by remember { mutableStateOf("https://github.com/mohityadav/") }

        AlertDialog(
            onDismissRequest = { showAddProjectDialog = false },
            title = { Text("Add Portfolio Project Card", color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Project Title") }, singleLine = true)
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, maxLines = 3)
                    OutlinedTextField(value = tagsStr, onValueChange = { tagsStr = it }, label = { Text("Tags (comma separated)") }, singleLine = true)
                    OutlinedTextField(value = liveUrl, onValueChange = { liveUrl = it }, label = { Text("Live Demo URL") }, singleLine = true)
                    OutlinedTextField(value = ghUrl, onValueChange = { ghUrl = it }, label = { Text("GitHub URL") }, singleLine = true)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val tags = tagsStr.split(",").map { it.trim() }.filter { it.isNotBlank() }
                        viewModel.addPortfolioProjectItem(title, desc, tags, liveUrl, ghUrl, "")
                        showAddProjectDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF002026))
                ) {
                    Text("Add Card")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddProjectDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = CyberSurfaceElevated
        )
    }

    // Add Dynamic CMS Field Dialog
    if (showAddFieldDialog) {
        var sectionKey by remember { mutableStateOf("announcements") }
        var fieldKey by remember { mutableStateOf("") }
        var fieldLabel by remember { mutableStateOf("") }
        var fieldType by remember { mutableStateOf("TEXT") }
        var defaultValue by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddFieldDialog = false },
            title = {
                Column {
                    Text("Add Custom CMS Field", color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text("Declare new fields for this website's CMS backend", color = CyberCyan, fontSize = 11.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = sectionKey,
                        onValueChange = { sectionKey = it },
                        label = { Text("Section Key (e.g. announcements, hero, cta)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_field_section"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = fieldKey,
                        onValueChange = {
                            fieldKey = it
                            if (fieldLabel.isBlank() && it.isNotBlank()) {
                                fieldLabel = it.replace("_", " ").replaceFirstChar { char -> char.uppercase() }
                            }
                        },
                        label = { Text("Field Key (e.g. alert_banner, discount)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_field_key"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = fieldLabel,
                        onValueChange = { fieldLabel = it },
                        label = { Text("Display Label (e.g. Global Banner Alert)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_field_label"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = fieldType,
                        onValueChange = { fieldType = it },
                        label = { Text("Field Type (TEXT, MULTILINE, NUMBER, IMAGE_URL)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_field_type"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = defaultValue,
                        onValueChange = { defaultValue = it },
                        label = { Text("Initial / Default Value") },
                        modifier = Modifier.fillMaxWidth().testTag("add_field_default"),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (fieldKey.isNotBlank() && fieldLabel.isNotBlank()) {
                            viewModel.addDynamicField(
                                projectId = projectId,
                                sectionKey = sectionKey,
                                fieldKey = fieldKey,
                                fieldLabel = fieldLabel,
                                fieldType = fieldType.uppercase(),
                                defaultValue = defaultValue
                            )
                            showAddFieldDialog = false
                        }
                    },
                    enabled = fieldKey.isNotBlank() && fieldLabel.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF002026)),
                    modifier = Modifier.testTag("confirm_add_field_btn")
                ) {
                    Text("Add Field to Schema", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddFieldDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = CyberSurfaceElevated
        )
    }
}

