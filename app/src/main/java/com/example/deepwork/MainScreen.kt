package com.example.deepwork

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deepwork.viewmodel.AppViewModel
import com.example.deepwork.views.LockScreenActivity
import kotlinx.coroutines.delay
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin


@Composable
fun MainScreen(appViewModel: AppViewModel) {
    val context = LocalContext.current
    val isDetoxActive by appViewModel.isDetoxActive.collectAsState()
    val emergencyUnlockCount by appViewModel.emergencyUnlockCount.collectAsState()
    var selectedDuration by remember { mutableStateOf(30f) }

    LaunchedEffect(isDetoxActive) {
        if (isDetoxActive) {
            context.startActivity(
                Intent(context, LockScreenActivity::class.java)
                    .apply {
                        putExtra("EXTRA_MINUTES", selectedDuration.toInt())
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    }
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
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
                        !isUsageAccessGranted(context) ->
                            context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                        !isAccessibilityServiceEnabled(context) ->
                            context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                        else ->
                            appViewModel.toggleDetox(context)
                    }
                }
            )
        }

        Spacer(Modifier.height(24.dp))

        Text("Emergency Unlocks Remaining: $emergencyUnlockCount")
    }
}

// Circular Timer
@Composable
fun CircularTimerSelector(
    modifier: Modifier = Modifier,
    maxMinutes: Int = 180,
    initialMinutes: Int = 30,
    onDurationChanged: (Int) -> Unit,
    onStartClicked: () -> Unit
) {
    var totalDuration by remember { mutableStateOf(initialMinutes * 60) } // in seconds
    var remainingSeconds by remember { mutableStateOf(totalDuration) }
    var isRunning by remember { mutableStateOf(false) }
    var angle by remember { mutableStateOf(360f * initialMinutes / maxMinutes) }
    var isEditing by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning, remainingSeconds) {
        if (isRunning && remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds--
            angle = 360f * remainingSeconds / totalDuration
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(300.dp)
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    val center = Offset(150f, 150f)
                    val touch = change.position
                    val dx = touch.x - center.x
                    val dy = touch.y - center.y
                    val theta = (atan2(dy, dx).toDegrees() + 360f) % 360f
                    angle = theta
                    val minutes = (maxMinutes * angle / 360f).roundToInt()
                    totalDuration = minutes * 60
                    remainingSeconds = totalDuration
                    isRunning = false
                    onDurationChanged(minutes)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = Stroke(width = 20f, cap = StrokeCap.Round)
            val radiusPx = size.minDimension / 2 - stroke.width / 2

            drawArc(
                color = Color.LightGray,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(0f, 0f),
                size = Size(size.width, size.height),
                style = stroke
            )

            drawArc(
                color = Color(0xFFFF9800),
                startAngle = -90f,
                sweepAngle = angle,
                useCenter = false,
                topLeft = Offset(0f, 0f),
                size = Size(size.width, size.height),
                style = stroke
            )

            val thetaRad = Math.toRadians(angle.toDouble() - 90)
            val thumbX = size.center.x + radiusPx * cos(thetaRad).toFloat()
            val thumbY = size.center.y + radiusPx * sin(thetaRad).toFloat()
            drawCircle(Color.Red, radius = 12f, center = Offset(thumbX, thumbY))
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (isEditing) {
                var input by remember { mutableStateOf((remainingSeconds / 60).toString()) }

                BasicTextField(
                    value = input,
                    onValueChange = {
                        input = it.filter { ch -> ch.isDigit() }
                        val minutes = input.toIntOrNull()?.coerceIn(1, maxMinutes) ?: 0
                        totalDuration = minutes * 60
                        remainingSeconds = totalDuration
                        angle = 360f * remainingSeconds / totalDuration
                        onDurationChanged(minutes)
                    },
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                )
            } else {
                val minutes = remainingSeconds / 60
                val seconds = remainingSeconds % 60
                Text(
                    text = String.format("%02d:%02d", minutes, seconds),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.clickable { isEditing = true }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isRunning) "" else "Start",
                fontSize = 16.sp,
                color = Color.Blue,
                modifier = Modifier.clickable {
                    if (!isRunning) {
                        onStartClicked()
                    }
                    isEditing = false
                }
            )
        }
    }
}

private fun Float.toDegrees(): Float = (Math.toDegrees(this.toDouble()) % 360f).toFloat()


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
