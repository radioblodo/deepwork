package com.example.deepwork.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

/**
 * A full-screen countdown timer with a circular progress ring for lock-screen usage.
 * @param modifier Modifier to apply
 * @param initialMinutes Starting duration in minutes
 * @param onStart Invoked when the user taps Play to begin countdown (use to trigger lockTask)
 * @param onFinish Invoked when countdown reaches zero (use to exit lock and finish Activity)
 */
@Composable
fun LockScreenTimer(
    modifier: Modifier = Modifier.fillMaxSize(),
    initialMinutes: Int = 30,
    onStart: () -> Unit,
    onFinish: () -> Unit
) {
    // Total duration in seconds
    val totalDuration = remember { initialMinutes * 60 }
    var remainingSeconds by remember { mutableStateOf(totalDuration) }
    var isRunning by remember { mutableStateOf(false) }
    // Angle for arc: full 360 when full, 0 when done
    var angle by remember { mutableStateOf(360f) }

    // Countdown effect
    LaunchedEffect(isRunning) {
        while (isRunning && remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds--
            angle = 360f * remainingSeconds / totalDuration
        }
        if (remainingSeconds <= 0) {
            isRunning = false
            onFinish()
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        // Draw circular progress
        Canvas(modifier = Modifier
            .size(300.dp)
            .pointerInput(Unit) {
                // disable dragging when running
                if (!isRunning) {
                    detectDragGestures { change, _ ->
                        // dragging disabled in lock-screen version
                    }
                }
            }
        ) {
            val stroke = Stroke(width = 20f, cap = StrokeCap.Round)
            drawArc(
                color = Color.LightGray,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset.Zero,
                size = Size(size.width, size.height),
                style = stroke
            )
            drawArc(
                color = Color(0xFFFF9800),
                startAngle = -90f,
                sweepAngle = angle,
                useCenter = false,
                topLeft = Offset.Zero,
                size = Size(size.width, size.height),
                style = stroke
            )
            // thumb position (optional, can hide)
            val radius = size.minDimension / 2 - stroke.width / 2
            val thetaRad = Math.toRadians(angle.toDouble() - 90)
            drawCircle(
                Color.Red,
                radius = 12f,
                center = Offset(
                    x = size.center.x + radius * cos(thetaRad).toFloat(),
                    y = size.center.y + radius * sin(thetaRad).toFloat()
                )
            )
        }

        // Center column: time display and play/pause
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Time text
            val minutes = remainingSeconds / 60
            val seconds = remainingSeconds % 60
            Text(
                text = String.format("%02d:%02d", minutes, seconds),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Play/Pause button
            Text(
                text = if (isRunning) "Pause" else "Play",
                fontSize = 18.sp,
                color = Color.Blue,
                modifier = Modifier
                    .clickable {
                        if (!isRunning && remainingSeconds == totalDuration) {
                            onStart()
                        }
                        isRunning = !isRunning
                    }
            )
        }
    }
}
