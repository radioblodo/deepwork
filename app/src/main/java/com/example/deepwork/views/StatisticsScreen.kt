// file: app/src/main/java/com/example/deepwork/views/StatisticsScreen.kt
package com.example.deepwork.views

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.deepwork.components.AppScaffold
import com.example.deepwork.components.StatisticsScreenContent
import com.example.deepwork.viewmodel.AppViewModel
import com.example.deepwork.viewmodel.LockSession
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StatisticsScreen(
   appViewModel: AppViewModel,
    padding: PaddingValues
){
    val history by appViewModel.lockHistory.collectAsState(emptyList())
    StatisticsScreenContent(history, padding)
}


