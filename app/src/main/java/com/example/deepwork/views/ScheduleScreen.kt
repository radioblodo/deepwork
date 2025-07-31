// file: app/src/main/java/com/example/deepwork/views/ScheduleScreen.kt
package com.example.deepwork.views

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.deepwork.viewmodel.AppViewModel

@Composable
fun ScheduleScreen(
    appViewModel: AppViewModel,
    padding: PaddingValues
) {
    // Grab the Android Context once in the composable
    val context = LocalContext.current

    // TODO: replace these with real state from your ViewModel
    var startHour by remember { mutableStateOf(9) }
    var startMinute by remember { mutableStateOf(0) }
    var endHour by remember { mutableStateOf(18) }
    var endMinute by remember { mutableStateOf(0) }

    // A plain Kotlin helper; takes Context as a parameter
    fun showTimePicker(
        context: Context,
        hour: Int,
        minute: Int,
        onTimeSet: (Int, Int) -> Unit
    ) {
        TimePickerDialog(
            context,
            { _, h, m -> onTimeSet(h, m) },
            hour,
            minute,
            true
        ).show()
    }

    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Daily Detox Schedule",
            style = MaterialTheme.typography.headlineMedium
        )

        // Start time row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showTimePicker(
                        context,
                        startHour,
                        startMinute
                    ) { h, m ->
                        startHour = h
                        startMinute = m
                        // TODO: appViewModel.setDetoxStart(h, m)
                    }
                }
                .padding(vertical = 8.dp)
        ) {
            Text("Start:", modifier = Modifier.weight(1f))
            Text(
                text = "%02d:%02d".format(startHour, startMinute),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Divider()

        // End time row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showTimePicker(
                        context,
                        endHour,
                        endMinute
                    ) { h, m ->
                        endHour = h
                        endMinute = m
                        // TODO: appViewModel.setDetoxEnd(h, m)
                    }
                }
                .padding(vertical = 8.dp)
        ) {
            Text("End:", modifier = Modifier.weight(1f))
            Text(
                text = "%02d:%02d".format(endHour, endMinute),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Divider()
    }
}
