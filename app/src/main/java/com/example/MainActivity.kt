package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Approval
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Approval
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MasterAdminDatabase
import com.example.data.repository.MasterAdminRepository
import com.example.ui.screens.AuditLogScreen
import com.example.ui.screens.ChangeRequestsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DeploymentsScreen
import com.example.ui.screens.MasterSetupScreen
import com.example.ui.screens.ProjectDetailScreen
import com.example.ui.screens.ProjectsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberCyanDark
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.MasterAdminTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MasterAdminViewModel
import com.example.ui.viewmodel.UiEvent

import com.example.ui.components.OwnerEnrollmentDialog

enum class NavigationDestination(val title: String, val testTag: String) {
    HOME("Home", "nav_item_home"),
    PROJECTS("Projects", "nav_item_projects"),
    SETUP("Setup Wizard", "nav_item_setup"),
    CHANGES("Changes", "nav_item_changes"),
    DEPLOYMENTS("Deploy", "nav_item_deploy"),
    SETTINGS("Settings", "nav_item_settings")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = MasterAdminDatabase.getDatabase(applicationContext)
        val repository = MasterAdminRepository(db.dao())

        setContent {
            com.example.ui.theme.MohitOSTheme {
                val viewModel = remember {
                    MasterAdminViewModel(repository)
                }
                MasterAdminApp(viewModel)
            }
        }
    }
}

@Composable
fun MasterAdminApp(viewModel: MasterAdminViewModel) {
    val context = LocalContext.current
    val ownerConfig by viewModel.ownerConfig.collectAsState()
    var currentDestination by remember { mutableStateOf(NavigationDestination.HOME) }
    var currentDetailProjectId by remember { mutableStateOf<String?>(null) }
    var showingAuditScreen by remember { mutableStateOf(false) }
    var showFirstTimeOwnerSetup by remember { mutableStateOf(false) }
    var isSplashVisible by remember { mutableStateOf(true) }

    LaunchedEffect(ownerConfig.isEnrolled) {
        if (!ownerConfig.isEnrolled) {
            showFirstTimeOwnerSetup = true
        }
    }

    if (isSplashVisible) {
        com.example.ui.screens.SplashScreen(
            onSplashFinished = { isSplashVisible = false }
        )
        return
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is UiEvent.ShowError -> {
                    Toast.makeText(context, "Error: ${event.error}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    if (showFirstTimeOwnerSetup) {
        OwnerEnrollmentDialog(
            viewModel = viewModel,
            onDismiss = { showFirstTimeOwnerSetup = false }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CyberBackground,
        contentWindowInsets = WindowInsets(0.dp),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (currentDetailProjectId == null && !showingAuditScreen) {
                NavigationBar(
                    windowInsets = WindowInsets.navigationBars,
                    containerColor = CyberSurfaceElevated,
                    contentColor = CyberCyan,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    val items = listOf(
                        Triple(NavigationDestination.HOME, Icons.Filled.Home, Icons.Outlined.Home),
                        Triple(NavigationDestination.PROJECTS, Icons.Filled.Folder, Icons.Outlined.Folder),
                        Triple(NavigationDestination.SETUP, Icons.Filled.Extension, Icons.Outlined.Extension),
                        Triple(NavigationDestination.CHANGES, Icons.Filled.Approval, Icons.Outlined.Approval),
                        Triple(NavigationDestination.DEPLOYMENTS, Icons.Filled.CloudUpload, Icons.Outlined.CloudUpload),
                        Triple(NavigationDestination.SETTINGS, Icons.Filled.Settings, Icons.Outlined.Settings)
                    )

                    items.forEach { (dest, filledIcon, outlinedIcon) ->
                        val isSelected = currentDestination == dest
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                currentDestination = dest
                                currentDetailProjectId = null
                                showingAuditScreen = false
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) filledIcon else outlinedIcon,
                                    contentDescription = dest.title
                                )
                            },
                            label = { Text(dest.title, fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CyberCyan,
                                unselectedIconColor = TextSecondary,
                                selectedTextColor = CyberCyan,
                                unselectedTextColor = TextMuted,
                                indicatorColor = CyberCyanDark
                            ),
                            modifier = Modifier.testTag(dest.testTag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                showingAuditScreen -> {
                    BackHandler { showingAuditScreen = false }
                    AuditLogScreen(viewModel)
                }
                currentDetailProjectId != null -> {
                    ProjectDetailScreen(
                        projectId = currentDetailProjectId!!,
                        viewModel = viewModel,
                        onBack = { currentDetailProjectId = null }
                    )
                }
                else -> {
                    when (currentDestination) {
                        NavigationDestination.HOME -> DashboardScreen(
                            viewModel = viewModel,
                            onNavigateToProject = { id -> currentDetailProjectId = id },
                            onNavigateToChanges = { currentDestination = NavigationDestination.CHANGES },
                            onNavigateToDeployments = { currentDestination = NavigationDestination.DEPLOYMENTS },
                            onNavigateToProjects = { currentDestination = NavigationDestination.PROJECTS },
                            onNavigateToAudit = { showingAuditScreen = true },
                            onNavigateToSetup = { currentDestination = NavigationDestination.SETUP }
                        )
                        NavigationDestination.PROJECTS -> ProjectsScreen(
                            viewModel = viewModel,
                            onNavigateToProject = { id -> currentDetailProjectId = id }
                        )
                        NavigationDestination.SETUP -> MasterSetupScreen(viewModel)
                        NavigationDestination.CHANGES -> ChangeRequestsScreen(viewModel)
                        NavigationDestination.DEPLOYMENTS -> DeploymentsScreen(viewModel)
                        NavigationDestination.SETTINGS -> SettingsScreen(viewModel)
                    }
                }
            }
        }
    }
}
