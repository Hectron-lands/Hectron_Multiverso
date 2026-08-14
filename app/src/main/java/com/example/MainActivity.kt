package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.AppDatabase
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.EventsScreen
import com.example.ui.screens.PlanetsScreen
import com.example.ui.screens.FactionsScreen
import com.example.ui.screens.LoreScreen
import com.example.ui.screens.LabsScreen
import com.example.ui.screens.SimulationScreen
import com.example.ui.screens.LegalScreen
import com.example.ui.screens.SubscriptionScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodels.UniverseViewModel
import com.example.viewmodels.UniverseViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val database = AppDatabase.getDatabase(this)
        
        setContent {
            MyApplicationTheme {
                val viewModel: UniverseViewModel = viewModel(
                    factory = UniverseViewModelFactory(database)
                )
                MainScreen(viewModel)
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: UniverseViewModel) {
    val navController = rememberNavController()

    val items = listOf(
        Screen.Dashboard,
        Screen.Planets,
        Screen.Engine,
        Screen.Factions,
        Screen.Lore,
        Screen.Labs
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = com.example.ui.theme.BgDark,
        bottomBar = {
            NavigationBar(
                containerColor = com.example.ui.theme.BgDark,
                contentColor = com.example.ui.theme.TextTertiary,
                tonalElevation = 0.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title.uppercase(), style = MaterialTheme.typography.labelSmall, maxLines = 1) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = com.example.ui.theme.Cyan400,
                            unselectedIconColor = com.example.ui.theme.TextTertiary,
                            selectedTextColor = com.example.ui.theme.Cyan400,
                            unselectedTextColor = com.example.ui.theme.TextTertiary,
                            indicatorColor = com.example.ui.theme.Cyan400.copy(alpha = 0.1f)
                        ),
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) { 
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToSubscriptions = { navController.navigate(Screen.Subscriptions.route) },
                    onNavigateToLegal = { navController.navigate(Screen.Legal.route) }
                ) 
            }
            composable(Screen.Planets.route) { PlanetsScreen(viewModel) }
            composable(Screen.Events.route) { EventsScreen(viewModel) }
            composable(Screen.Engine.route) { SimulationScreen() }
            composable(Screen.Factions.route) { FactionsScreen(viewModel) }
            composable(Screen.Lore.route) { LoreScreen(viewModel) }
            composable(Screen.Labs.route) { LabsScreen() }
            composable(Screen.Legal.route) { LegalScreen() }
            composable(Screen.Subscriptions.route) { SubscriptionScreen() }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Dashboard : Screen("dashboard", "Hub", Icons.Default.Dashboard)
    object Planets : Screen("planets", "Cosmos", Icons.Default.Public)
    object Events : Screen("events", "Events", Icons.Default.Event)
    object Engine : Screen("engine", "Engine", Icons.Default.Memory)
    object Factions : Screen("factions", "Vault", Icons.Default.Group)
    object Lore : Screen("lore", "Lore", Icons.Default.LibraryBooks)
    object Labs : Screen("labs", "Labs", Icons.Default.Build)
    object Legal : Screen("legal", "Legal", Icons.Default.Public)
    object Subscriptions : Screen("subscriptions", "Plans", Icons.Default.Public)
}
