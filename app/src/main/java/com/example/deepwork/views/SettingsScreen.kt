// file: app/src/main/java/com/example/deepwork/views/SettingsScreen.kt
package com.example.deepwork.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.deepwork.components.AppScaffold
import com.example.deepwork.viewmodel.AppViewModel

private data class SettingsOption(
    val title: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun SettingsScreen(
    appViewModel: AppViewModel,
    navController: NavController,
    padding: PaddingValues
) {
    // define your menu items
    val options = listOf(
        SettingsOption("Whitelist Apps", Icons.Default.Star, Screen.Whitelist.route),
        SettingsOption("Schedule Detox", Icons.Default.Schedule, Screen.Schedule.route),
        SettingsOption("Privacy",       Icons.Default.Lock,   Screen.Privacy.route)
    )

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            items(options) { opt ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate(opt.route)
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = opt.icon,
                        contentDescription = opt.title,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = opt.title,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Divider()
            }
        }
}
