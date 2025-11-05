package com.dice.focusflow.feature.settings

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings_prefs")

class SettingsRepository(private val context: Context) {

    companion object {
        private val KEY_FOCUS_MINUTES = intPreferencesKey("focus_minutes")
        private val KEY_SHORT_BREAK = intPreferencesKey("short_break_minutes")
        private val KEY_LONG_BREAK = intPreferencesKey("long_break_minutes")
        private val KEY_SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        private val KEY_VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
    }

    val settingsFlow: Flow<SettingsUiState> = context.dataStore.data.map { prefs ->
        SettingsUiState(
            focusMinutes = prefs[KEY_FOCUS_MINUTES] ?: 25,
            shortBreakMinutes = prefs[KEY_SHORT_BREAK] ?: 5,
            longBreakMinutes = prefs[KEY_LONG_BREAK] ?: 15,
            soundEnabled = prefs[KEY_SOUND_ENABLED] ?: true,
            vibrationEnabled = prefs[KEY_VIBRATION_ENABLED] ?: true
        )
    }

    suspend fun saveSettings(state: SettingsUiState) {
        context.dataStore.edit { prefs ->
            prefs[KEY_FOCUS_MINUTES] = state.focusMinutes
            prefs[KEY_SHORT_BREAK] = state.shortBreakMinutes
            prefs[KEY_LONG_BREAK] = state.longBreakMinutes
            prefs[KEY_SOUND_ENABLED] = state.soundEnabled
            prefs[KEY_VIBRATION_ENABLED] = state.vibrationEnabled
        }
    }
}
