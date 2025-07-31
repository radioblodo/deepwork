package com.example.deepwork.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.deepwork.views.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold (
    title: String,
    navController: NavController,
    startDestination: String,
    drawerState: DrawerState = androidx.compose.material3.rememberDrawerState(initialValue = androidx.compose.material3.DrawerValue.Closed),
    content: @Composable (PaddingValues) -> Unit
) {
    val scope = rememberCoroutineScope()

    // Phone Screen Dimension
    val configuration = LocalConfiguration.current
    val halfScreenWidth = configuration.screenWidthDp / 2

    // NavHost
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(halfScreenWidth.dp)
            ) {
                Text("Menu", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
                Divider()
                Column(
                    modifier = Modifier.padding(vertical = 24.dp),
                    verticalArrangement= Arrangement.spacedBy(16.dp)
                ){
                NavigationDrawerItem(
                    label = { Text("Home") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Go Home Screen"
                        )
                    },
                    selected = currentRoute == Screen.Home.route,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Home.route) {
                            popUpTo(startDestination) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Statistics") },
                    selected = currentRoute == Screen.Statistics.route,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "View Statistics"
                        )
                    },
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate(Screen.Statistics.route) {
                                popUpTo(startDestination) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = currentRoute == Screen.Settings.route,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "App Settings"
                        )
                    },
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate(Screen.Settings.route) {
                                popUpTo(startDestination) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { padding ->
            content(padding)
        }
    }
}