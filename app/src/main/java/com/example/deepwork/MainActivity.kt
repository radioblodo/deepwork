package com.example.deepwork

import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deepwork.ui.theme.DeepWorkTheme
import com.example.deepwork.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DeepWorkTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val appViewModel: AppViewModel = viewModel()
                    MainScreen(appViewModel = appViewModel)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(appViewModel: AppViewModel) {
    val context = LocalContext.current
    val isDetoxActive by appViewModel.isDetoxActive.collectAsState()
    val emergencyUnlockCount by appViewModel.emergencyUnlockCount.collectAsState()

    LaunchedEffect(Unit) {
        appViewModel.loadInitialState()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Digital Detox") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isDetoxActive) "Detox is ACTIVE" else "Detox is INACTIVE",
                style = MaterialTheme.typography.headlineMedium,
                color = if (isDetoxActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    // In a real app, you'd check accessibility service status here
                    if (!isAccessibilityServiceEnabled(context, "com.yourpackage.appname.services.AppBlockingService")) {
                        Toast.makeText(context, "Please enable Accessibility Service first!", Toast.LENGTH_LONG).show()
                        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        context.startActivity(intent)
                    } else {
                        appViewModel.toggleDetox(context)
                        Toast.makeText(context, "Detox Toggled!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isDetoxActive) "Stop Detox" else "Start Detox")
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { /* TODO: Navigate to App Whitelist Settings */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("App Whitelist Settings")
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { /* TODO: Navigate to Call Whitelist Settings */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Call Whitelist Settings")
            }
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Emergency Unlocks Remaining: $emergencyUnlockCount",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    appViewModel.initiatePaywallUnlock(context) {
                        Toast.makeText(context, "Opening Play Store for purchase...", Toast.LENGTH_SHORT).show()
                        // In a real app, on successful purchase, setPremiumUnlocked(true) would be called
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Text("Unlock Permanently (Paywall)")
            }
        }
    }
}

// Helper function to check if accessibility service is enabled (basic check)
@SuppressLint("ServiceCast")
fun isAccessibilityServiceEnabled(context: Context, serviceName: String): Boolean {
    val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
    for (service in enabledServices) {
        if (service.id.contains(context.packageName) && service.id.contains(serviceName)) {
            return true
        }
    }
    return false
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    DeepWorkTheme {
        MainScreen(appViewModel = AppViewModel())
    }
}