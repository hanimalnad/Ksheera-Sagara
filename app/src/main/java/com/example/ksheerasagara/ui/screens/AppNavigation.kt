package com.example.ksheerasagara.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.ksheerasagara.ui.theme.GreenPrimary
import com.example.ksheerasagara.viewmodel.DairyViewModel

data class NavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(onLogout: () -> Unit) {
    val navController      = rememberNavController()
    val vm: DairyViewModel = viewModel()

    // ── Only 5 items in bottom nav — Settings moved to top bar ──
    val items = listOf(
        NavItem("dashboard", Icons.Default.Dashboard, "Home"),
        NavItem("milk",      Icons.Default.Opacity,   "Milk"),
        NavItem("expense",   Icons.Default.Receipt,   "Expenses"),
        NavItem("cows",      Icons.Default.Pets,      "Cows"),
        NavItem("analytics", Icons.Default.BarChart,  "Analytics")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Ksheera-Sagara",
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color      = GreenPrimary
                    )
                },
                actions = {
                    // ── Settings icon top right ──
                    IconButton(onClick = {
                        navController.navigate("settings") {
                            launchSingleTop = true
                        }
                    }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = GreenPrimary
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                val current = navController
                    .currentBackStackEntryAsState().value
                    ?.destination?.route

                items.forEach { item ->
                    NavigationBarItem(
                        icon     = { Icon(item.icon, contentDescription = item.label) },
                        label    = { Text(item.label, fontSize = 11.sp) },
                        selected = current == item.route,
                        onClick  = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(navController, startDestination = "dashboard") {
            composable("dashboard") { DashboardScreen(vm, padding) }
            composable("milk")      { MilkEntryScreen(vm, padding) }
            composable("expense")   { ExpenseScreen(vm, padding)   }
            composable("cows")      { CowScreen(vm, padding)       }
            composable("analytics") { AnalyticsScreen(vm, padding) }
            // Settings not in bottom nav but still navigable
            composable("settings")  {
                SettingsScreen(padding = padding, onLogout = onLogout)
            }
        }
    }
}