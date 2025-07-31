package com.example.deepwork.views

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Statistics : Screen("statistics")
    object Settings : Screen("settings")
    object Whitelist : Screen("settings/whitelist")
    object Schedule : Screen("settings/schedule")
    object Privacy : Screen("settings/privacy")

}