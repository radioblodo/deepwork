// file: app/src/main/java/com/example/deepwork/viewmodel/AppViewModel.kt
package com.example.deepwork.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel holding all of your app’s shared UI state:
 *  - detox on/off
 *  - emergency unlock count
 *  - premium‐unlocked flag
 *  - history of past lock sessions
 */
class AppViewModel : ViewModel() {

    // --- Detox State -------------------------------------------------------
    private val _isDetoxActive = MutableStateFlow(false)
    val isDetoxActive: StateFlow<Boolean> = _isDetoxActive.asStateFlow()

    // --- Emergency Unlocks ------------------------------------------------
    private val _emergencyUnlockCount = MutableStateFlow(3)
    val emergencyUnlockCount: StateFlow<Int> = _emergencyUnlockCount.asStateFlow()

    // --- Premium Purchase Flag --------------------------------------------
    private val _isPremiumUnlocked = MutableStateFlow(false)
    val isPremiumUnlocked: StateFlow<Boolean> = _isPremiumUnlocked.asStateFlow()

    // --- Lock Session History ---------------------------------------------
    private val _lockHistory = MutableStateFlow<List<LockSession>>(emptyList())
    val lockHistory: StateFlow<List<LockSession>> = _lockHistory.asStateFlow()

    // -----------------------------------------------------------------------

    /** Toggle the detox mode on or off. */
    fun toggleDetox(context: Context) {
        viewModelScope.launch {
            _isDetoxActive.value = !_isDetoxActive.value
            // TODO: start/stop your blocking service here
            // TODO: persist to preferences
        }
    }

    /**
     * Consume one emergency unlock. Calls onSuccess if there were unlocks left,
     * otherwise onFailure.
     */
    fun useEmergencyUnlock(
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        viewModelScope.launch {
            if (_emergencyUnlockCount.value > 0) {
                _emergencyUnlockCount.update { it - 1 }
                onSuccess()
                // TODO: pause blocking service, persist new count
            } else {
                onFailure()
            }
        }
    }

    /**
     * Kick off your billing flow for premium unlock.
     * onPurchaseInitiated is called immediately to let the UI react.
     */
    fun initiatePaywallUnlock(
        context: Context,
        onPurchaseInitiated: () -> Unit
    ) {
        viewModelScope.launch {
            onPurchaseInitiated()
            // TODO: call BillingManager.launchPurchaseFlow(context)
            //       and then on success:
            //       _isPremiumUnlocked.value = true
        }
    }

    /** Mark the user as premium unlocked (e.g. after successful purchase or restore). */
    fun setPremiumUnlocked(isUnlocked: Boolean) {
        _isPremiumUnlocked.value = isUnlocked
        // TODO: persist to preferences
    }

    /**
     * Record a completed lock session in history.
     * Call this when the user’s lock finishes or they manually stop detox.
     */
    fun recordLockSession(session: LockSession) {
        _lockHistory.update { previous ->
            previous + session
        }
        // TODO: persist history if you want across restarts
    }

    /** Load data from persistence (call at app start). */
    fun loadInitialState() {
        viewModelScope.launch {
            // TODO:
            //  _isDetoxActive.value        = AppPreferences.getDetoxStatus()
            //  _emergencyUnlockCount.value = AppPreferences.getUnlockCount()
            //  _isPremiumUnlocked.value    = AppPreferences.isPremium()
            //  _lockHistory.value          = AppPreferences.getLockHistory()
        }
    }
}
