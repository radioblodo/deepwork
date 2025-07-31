package com.example.deepwork.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

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

    LaunchedEffect(isRunning, remainingSeconds, isEditing) {
        if (isRunning && remainingSeconds > 0 && !isEditing) {
            delay(1000L)
            remainingSeconds--
            angle = 360f * remainingSeconds / totalDuration
        }
    }
    var previousTheta by remember { mutableStateOf(angle) }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(150.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        isEditing = true
                    },
                    onDragEnd = {
                        isEditing = false
                    }
                )
                { change, _ ->
                    val center = Offset(75f, 75f)
                    val touch = change.position
                    val dx = touch.x - center.x
                    val dy = touch.y - center.y
                    val newTheta = (atan2(dy, dx).toDegrees() + 360f) % 360f
                    val delta = ((newTheta - previousTheta + 540f) % 360f) - 180f
                    val nextAngle = (angle + delta).coerceIn(0f, 360f)
                    if ((angle == 0f && delta < 0) || (angle == 360f && delta > 0)) {
                        return@detectDragGestures
                    }
                    angle = nextAngle
                    previousTheta = newTheta
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

            // Background arc
            drawArc(
                color = Color.LightGray,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(0f, 0f),
                size = Size(size.width, size.height),
                style = stroke
            )

            // Colored arc
            drawArc(
                color = Color(0xFFFF9800),
                startAngle = -90f,
                sweepAngle = angle,
                useCenter = false,
                topLeft = Offset(0f, 0f),
                size = Size(size.width, size.height),
                style = stroke
            )

            // Thumb Dimensions
            val thumbRadiusPx = 15f
            val pathRadius = size.width / 2
            val thetaRad = Math.toRadians(angle.toDouble() - 90)
            val thumbX = size.center.x + pathRadius * cos(thetaRad).toFloat()
            val thumbY = size.center.y + pathRadius * sin(thetaRad).toFloat()
            drawCircle(
                Color.Red,
                radius = thumbRadiusPx,
                center = Offset(thumbX, thumbY)
            )
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