package com.example.deepwork

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.deepwork.ui.theme.DeepWorkTheme
import com.example.deepwork.viewmodel.AppViewModel
import com.example.deepwork.views.OnboardingScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DeepWorkTheme {
                var showOnboarding by rememberSaveable {mutableStateOf(true)}
                if (showOnboarding) {
                    OnboardingScreen {
                        showOnboarding = false
                    }
                } else {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val appViewModel: AppViewModel = viewModel()
                    val navController: NavController = rememberNavController()
                    MainScreen(
                        appViewModel = appViewModel,
                        navController = navController
                    )
                }
            }
        }
    }
}
}


