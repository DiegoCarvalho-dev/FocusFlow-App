package com.dice.focusflow.feature.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class AppThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}
data class UserSettings(
    val focusMinutes: Int = 25,
    val shortBreakMinutes: Int = 5,
    val longBreakMinutes: Int = 15,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM
)

val Context.userSettingsDataStore by preferencesDataStore(
    name = "user_settings"
)
object UserSettingsStore {

    private object Keys {
        val FOCUS_MIN = intPreferencesKey("focus_minutes")
        val SHORT_BREAK_MIN = intPreferencesKey("short_break_minutes")
        val LONG_BREAK_MIN = intPreferencesKey("long_break_minutes")

        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")

        val THEME_MODE = stringPreferencesKey("theme_mode")
    }
    fun observe(context: Context): Flow<UserSettings> {
        return context.userSettingsDataStore.data.map { prefs ->
            val focus = prefs[Keys.FOCUS_MIN] ?: 25
            val shortBreak = prefs[Keys.SHORT_BREAK_MIN] ?: 5
            val longBreak = prefs[Keys.LONG_BREAK_MIN] ?: 15
            val sound = prefs[Keys.SOUND_ENABLED] ?: true
            val vibration = prefs[Keys.VIBRATION_ENABLED] ?: true
            val themeName = prefs[Keys.THEME_MODE] ?: AppThemeMode.SYSTEM.name

            UserSettings(
                focusMinutes = focus,
                shortBreakMinutes = shortBreak,
                longBreakMinutes = longBreak,
                soundEnabled = sound,
                vibrationEnabled = vibration,
                themeMode = when (themeName) {
                    AppThemeMode.LIGHT.name -> AppThemeMode.LIGHT
                    AppThemeMode.DARK.name -> AppThemeMode.DARK
                    else -> AppThemeMode.SYSTEM
                }
            )
        }
    }


    suspend fun setFocusMinutes(context: Context, minutes: Int) {
        val safe = minutes.coerceIn(1, 180)
        context.userSettingsDataStore.edit { prefs ->
            prefs[Keys.FOCUS_MIN] = safe
        }
    }

    suspend fun setShortBreakMinutes(context: Context, minutes: Int) {
        val safe = minutes.coerceIn(1, 60)
        context.userSettingsDataStore.edit { prefs ->
            prefs[Keys.SHORT_BREAK_MIN] = safe
        }
    }

    suspend fun setLongBreakMinutes(context: Context, minutes: Int) {
        val safe = minutes.coerceIn(1, 60)
        context.userSettingsDataStore.edit { prefs ->
            prefs[Keys.LONG_BREAK_MIN] = safe
        }
    }

    suspend fun setSoundEnabled(context: Context, enabled: Boolean) {
        context.userSettingsDataStore.edit { prefs ->
            prefs[Keys.SOUND_ENABLED] = enabled
        }
    }

    suspend fun setVibrationEnabled(context: Context, enabled: Boolean) {
        context.userSettingsDataStore.edit { prefs ->
            prefs[Keys.VIBRATION_ENABLED] = enabled
        }
    }

    suspend fun setThemeMode(context: Context, mode: AppThemeMode) {
        context.userSettingsDataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = mode.name
        }
    }
}
