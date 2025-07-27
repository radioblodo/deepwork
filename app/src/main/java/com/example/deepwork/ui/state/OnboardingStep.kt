package com.example.deepwork.ui.state

sealed class OnboardingStep {
    object UsageAccess: OnboardingStep()
    object Accessibility: OnboardingStep()
    object DeviceAdmin: OnboardingStep()
    object Done: OnboardingStep()
}