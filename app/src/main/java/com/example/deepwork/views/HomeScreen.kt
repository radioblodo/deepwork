package com.example.deepwork.views

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.deepwork.components.CircularTimerSelector
import com.example.deepwork.viewmodel.AppViewModel

@Composable
fun HomeScreen(
    appViewModel: AppViewModel,
    navController: NavController,
    padding: PaddingValues
) {
    val isDetoxActive by appViewModel.isDetoxActive.collectAsState()
    val emergencyUnlockCount by appViewModel.emergencyUnlockCount.collectAsState()
    val context = LocalContext.current
    var selectedDuration by remember { mutableStateOf(30f) }

    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isDetoxActive) "Deepwork is active" else "How long do you want to lock your phone for?",
            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
            textAlign = TextAlign.Center
        )

        if (!isDetoxActive) {
            Spacer(Modifier.height(16.dp))

            CircularTimerSelector(
                initialMinutes = selectedDuration.toInt(),
                onDurationChanged = { selectedDuration = it.toFloat() },
                onStartClicked = {
                    when {
                        !isUsageAccessGranted(context) -> context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                        !isAccessibilityServiceEnabled(context) -> context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                        else -> appViewModel.toggleDetox(context)
                    }
                }
            )
        }
        Spacer(Modifier.height(24.dp))
        Text("Emergency Unlocks Remaining: $emergencyUnlockCount")
    }
}

// Helper function to check if usage access is granted
fun isUsageAccessGranted(context: Context): Boolean {
    val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    val now = System.currentTimeMillis()
    val stats = usm.queryUsageStats(
        UsageStatsManager.INTERVAL_DAILY,
        now - 1000 * 60,
        now
    )
    return !stats.isNullOrEmpty()
}

// Helper function to check if accessibility service is enabled
fun isAccessibilityServiceEnabled(context: Context): Boolean {
    val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
    return enabledServices.any { it.id.contains(context.packageName) }
}