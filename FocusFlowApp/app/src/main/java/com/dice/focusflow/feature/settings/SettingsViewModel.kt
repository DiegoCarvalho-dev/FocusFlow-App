package com.dice.focusflow.feature.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repo = SettingsRepository(application)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repo.settingsFlow.collectLatest { saved ->
                _uiState.value = saved
            }
        }
    }

    private fun updateState(block: (SettingsUiState) -> SettingsUiState) {
        val updated = block(_uiState.value)
        _uiState.value = updated
        viewModelScope.launch {
            repo.saveSettings(updated)
        }
    }

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
}
