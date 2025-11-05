package com.dice.focusflow.feature.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun updateFocusMinutes(value: Int) {
        val safe = value.coerceIn(1, 120)
        updateState { it.copy(focusMinutes = safe) }
    }

    fun updateShortBreakMinutes(value: Int) {
        val safe = value.coerceIn(1, 60)
        updateState { it.copy(shortBreakMinutes = safe) }
    }

    fun updateLongBreakMinutes(value: Int) {
        val safe = value.coerceIn(1, 60)
        updateState { it.copy(longBreakMinutes = safe) }
    }

    fun updateThemeMode(mode: ThemeMode) {
        updateState { it.copy(themeMode = mode) }
    }

    fun updateSoundEnabled(enabled: Boolean) {
        updateState { it.copy(soundEnabled = enabled) }
    }

    fun updateVibrationEnabled(enabled: Boolean) {
        updateState { it.copy(vibrationEnabled = enabled) }
    }

    private fun updateState(block: (SettingsUiState) -> SettingsUiState) {
        _uiState.value = block(_uiState.value)

    }
}
