// file: app/src/main/java/com/example/deepwork/views/WhitelistScreen.kt
package com.example.deepwork.views

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.deepwork.viewmodel.AppViewModel

@Composable
fun WhitelistScreen(
    appViewModel: AppViewModel,
    padding: PaddingValues
) {
    val context = LocalContext.current
    val pm = context.packageManager

    // TODO: replace with your actual VM-backed flows
    val installedApps = remember {
        pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
            .sortedBy { it.loadLabel(pm).toString() }
    }
    var whitelist by remember { mutableStateOf(setOf<String>()) }

    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
    ) {
        Text(
            text = "Whitelisted Apps",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
        Divider()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(installedApps) { appInfo ->
                val pkg = appInfo.packageName
                val label = appInfo.loadLabel(pm).toString()
                val checked = pkg in whitelist

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val next = if (checked) whitelist - pkg else whitelist + pkg
                            whitelist = next
                            // TODO: appViewModel.setWhitelisted(pkg, !checked)
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = {
                            whitelist = if (it) whitelist + pkg else whitelist - pkg
                            // TODO: appViewModel.setWhitelisted(pkg, it)
                        }
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(text = label, style = MaterialTheme.typography.bodyLarge)
                }
                Divider()
            }
        }
    }
}
