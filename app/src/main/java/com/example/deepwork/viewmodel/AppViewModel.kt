package com.example.deepwork.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// This ViewModel would interact with your AppPreferences and other services
class AppViewModel : ViewModel() {

    // --- State for MainActivity ---
    private val _isDetoxActive = MutableStateFlow(false)
    val isDetoxActive: StateFlow<Boolean> = _isDetoxActive.asStateFlow()

    private val _emergencyUnlockCount = MutableStateFlow(3) // Initial count
    val emergencyUnlockCount: StateFlow<Int> = _emergencyUnlockCount.asStateFlow()

    // --- State for LockScreenActivity ---
    // This could also be managed by passing via Intent or a shared singleton state
    // For simplicity, let's assume it's also accessible from here,
    // or passed to LockScreenActivity's ViewModel if it had one.
    private val _isPremiumUnlocked = MutableStateFlow(false)
    val isPremiumUnlocked: StateFlow<Boolean> = _isPremiumUnlocked.asStateFlow()


    // --- Actions/Functions ---
    fun toggleDetox(context: android.content.Context) {
        viewModelScope.launch {
            val newState = !_isDetoxActive.value
            _isDetoxActive.value = newState
            // In a real app:
            // - If newState is true, start AppBlockingService
            // - If newState is false, stop AppBlockingService
            // You'd also save this state to AppPreferences
        }
    }

    fun useEmergencyUnlock(onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            if (_emergencyUnlockCount.value > 0) {
                _emergencyUnlockCount.value--
                onSuccess()
                // In a real app:
                // - Temporarily disable blocking (e.g., stop/restart service with a flag)
                // - Save new count to AppPreferences
            } else {
                onFailure()
            }
        }
    }

    fun initiatePaywallUnlock(context: android.content.Context, onPurchaseInitiated: () -> Unit) {
        viewModelScope.launch {
            // In a real app:
            // - Call your BillingManager to start the purchase flow
            // - BillingManager would update _isPremiumUnlocked on successful purchase
            onPurchaseInitiated()
            println("Initiating paywall unlock...")
        }
    }

    fun setPremiumUnlocked(isUnlocked: Boolean) {
        _isPremiumUnlocked.value = isUnlocked
        // In a real app, save this to AppPreferences
    }

    // Function to simulate backend updating the count (e.g., after boot, or app resume)
    fun loadInitialState() {
        viewModelScope.launch {
            // In a real app, load from AppPreferences
            // _isDetoxActive.value = AppPreferences.getDetoxStatus()
            // _emergencyUnlockCount.value = AppPreferences.getEmergencyUnlockCount()
            // _isPremiumUnlocked.value = AppPreferences.isPremiumUnlocked()
            println("Loading initial state...")
        }
    }
}