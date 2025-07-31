// file: app/src/main/java/com/example/deepwork/views/PrivacyScreen.kt
package com.example.deepwork.views

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.deepwork.viewmodel.AppViewModel

@Composable
fun PrivacyScreen(
    appViewModel: AppViewModel,
    padding: PaddingValues
) {
    // TODO: sync this with your ViewModel
    var shareData by remember { mutableStateOf(false) }
    val ctx = LocalContext.current

    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Privacy Settings", style = MaterialTheme.typography.headlineMedium)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Share anonymous usage data", modifier = Modifier.weight(1f))
            Switch(
                checked = shareData,
                onCheckedChange = {
                    shareData = it
                    // TODO: appViewModel.setShareUsageData(it)
                }
            )
        }
        Divider()
        Text(
            "View Privacy Policy",
            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    ctx.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://your.app/privacypolicy"))
                    )
                }
                .padding(vertical = 8.dp)
        )
        Divider()
    }
}
