package com.example.deepwork

// Compose Navigation
import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.deepwork.components.AppScaffold
import com.example.deepwork.viewmodel.AppViewModel
import com.example.deepwork.views.HomeScreen
import com.example.deepwork.views.LockScreenActivity
import com.example.deepwork.views.PrivacyScreen
import com.example.deepwork.views.ScheduleScreen
import com.example.deepwork.views.Screen
import com.example.deepwork.views.SettingsScreen
import com.example.deepwork.views.StatisticsScreen
import com.example.deepwork.views.WhitelistScreen


@Composable
fun MainScreen(
    appViewModel: AppViewModel,
    navController: NavController
) {
    val isDetoxActive by appViewModel.isDetoxActive.collectAsState()
    val context = LocalContext.current
    var selectedDuration by remember { mutableStateOf(30f) }



    LaunchedEffect(isDetoxActive) {
        if (isDetoxActive) {
            context.startActivity(
                Intent(context, LockScreenActivity::class.java).apply {
                    putExtra("EXTRA_MINUTES", selectedDuration.toInt())
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
            )
        }
    }
    AppScaffold(
        title = "Deepwork",
        navController = navController,
        startDestination = Screen.Home.route
    ) { padding ->
        NavHost(
            navController = navController as NavHostController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    appViewModel,
                    navController,
                    padding = padding
                )
            }
            composable(Screen.Statistics.route) {
                StatisticsScreen(
                    appViewModel = appViewModel,
                    padding = padding
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    appViewModel = appViewModel,
                    navController = navController,
                    padding = padding
                )
            }
            composable(Screen.Whitelist.route) {
                WhitelistScreen(
                    appViewModel = appViewModel,
                    padding = padding
                )
            }
            composable(Screen.Schedule.route) {
                ScheduleScreen(
                    appViewModel = appViewModel,
                    padding = padding
                )
            }
            composable(Screen.Privacy.route) {
                PrivacyScreen(
                    appViewModel = appViewModel,
                    padding = padding
                )
            }
        }
    }
}






