package com.dice.focusflow.feature.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val focusMinutes: Int = 25,
    val shortBreakMinutes: Int = 5,
    val longBreakMinutes: Int = 15,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val isLoading: Boolean = true
)

class SettingsViewModel(
    app: Application
) : AndroidViewModel(app) {

    private val appContext = app.applicationContext

    val uiState: StateFlow<SettingsUiState> =
        UserSettingsStore.observe(appContext)
            .map { settings ->
                SettingsUiState(
                    focusMinutes = settings.focusMinutes,
                    shortBreakMinutes = settings.shortBreakMinutes,
                    longBreakMinutes = settings.longBreakMinutes,
                    soundEnabled = settings.soundEnabled,
                    vibrationEnabled = settings.vibrationEnabled,
                    themeMode = settings.themeMode,
                    isLoading = false
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = SettingsUiState()
            )

    fun updateFocusMinutes(min: Int) {
        viewModelScope.launch {
            UserSettingsStore.setFocusMinutes(appContext, min)
        }
    }

    fun updateShortBreakMinutes(min: Int) {
        viewModelScope.launch {
            UserSettingsStore.setShortBreakMinutes(appContext, min)
        }
    }

    fun updateLongBreakMinutes(min: Int) {
        viewModelScope.launch {
            UserSettingsStore.setLongBreakMinutes(appContext, min)
        }
    }

    fun updateSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            UserSettingsStore.setSoundEnabled(appContext, enabled)
        }
    }

    fun updateVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            UserSettingsStore.setVibrationEnabled(appContext, enabled)
        }
    }

    fun updateThemeMode(mode: AppThemeMode) {
        viewModelScope.launch {
            UserSettingsStore.setThemeMode(appContext, mode)
        }
    }
}
