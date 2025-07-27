package com.example.deepwork.views

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deepwork.ui.theme.DeepWorkTheme
import com.example.deepwork.viewmodel.AppViewModel

class LockScreenActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Prevent lock screen from being swiped away
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_FULLSCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        )
        setShowWhenLocked(true)
        setTurnScreenOn(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!isInLockTaskMode()) {
                startLockTask()
            }
        }
        setContent {
            DeepWorkTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface // Or a distinct lock screen color
                ) {
                    val appViewModel: AppViewModel = viewModel()
                    LockScreen(
                        appViewModel = appViewModel,
                        onUnlockSuccess = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                stopLockTask()
                            }
                            // In a real app, this would tell the AppBlockingService
                            // to temporarily stop blocking or allow access.
                            // For this example, we just finish the activity.
                            Toast.makeText(this, "Emergency unlock used!", Toast.LENGTH_SHORT).show()
                            finish() // Close the lock screen
                        }
                    )
                }
            }
        }
    }

    // Override onBackPressed to prevent easy bypass
    @SuppressLint("MissingSuperCall", "GestureBackNavigation")
    override fun onBackPressed() {
        Toast.makeText(this, "Stay focused! You cannot exit right now.", Toast.LENGTH_SHORT).show()
    }

}

@Composable
fun LockScreen(
    appViewModel: AppViewModel,
    onUnlockSuccess: () -> Unit
) {
    val context = LocalContext.current
    val emergencyUnlockCount by appViewModel.emergencyUnlockCount.collectAsState()
    val isPremiumUnlocked by appViewModel.isPremiumUnlocked.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.errorContainer) // A distinct color for locked state
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "PHONE LOCKED FOR DETOX!",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onErrorContainer,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Take a break. Focus on what truly matters.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onErrorContainer,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(48.dp))

        // Emergency Unlock Button
        Button(
            onClick = {
                if (isPremiumUnlocked) {
                    Toast.makeText(context, "Premium unlocked! Access granted.", Toast.LENGTH_SHORT).show()
                    onUnlockSuccess() // Allow immediate unlock if premium
                } else {
                    appViewModel.useEmergencyUnlock(
                        onSuccess = {
                            onUnlockSuccess()
                        },
                        onFailure = {
                            Toast.makeText(context, "No emergency unlocks left!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            if (isPremiumUnlocked) {
                Text("Access Phone (Premium)")
            } else {
                Text("Emergency Unlock ($emergencyUnlockCount left)")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Paywall Unlock Button
        if (!isPremiumUnlocked) {
            OutlinedButton(
                onClick = {
                    appViewModel.initiatePaywallUnlock(context) {
                        Toast.makeText(context, "Redirecting to Play Store...", Toast.LENGTH_SHORT).show()
                        // In a real app, on successful purchase,
                        // appViewModel.setPremiumUnlocked(true) would be called
                        // and this screen would react to it.
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onErrorContainer),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(MaterialTheme.colorScheme.onErrorContainer))
            ) {
                Text("Unlock Permanently (Pay)")
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun PreviewLockScreen() {
    DeepWorkTheme {
        LockScreen(appViewModel = AppViewModel(), onUnlockSuccess = {})
    }
}

fun Activity.isInLockTaskMode(): Boolean {
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        activityManager.lockTaskModeState != android.app.ActivityManager.LOCK_TASK_MODE_NONE
    } else {
        activityManager.isInLockTaskMode
    }
}
