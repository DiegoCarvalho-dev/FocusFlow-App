package com.dice.focusflow.feature.summary

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// DataStore atrelado ao contexto da aplicação
private val Context.dailySummaryDataStore by preferencesDataStore(name = "daily_summary")

class DailySummaryRepository(
    private val context: Context
) {

    companion object {
        private val KEY_DATE = stringPreferencesKey("summary_date")
        private val KEY_FOCUS_SECONDS = longPreferencesKey("summary_focus_seconds")
        private val KEY_COMPLETED_SESSIONS = intPreferencesKey("summary_completed_sessions")
    }

    private fun todayString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    val summaryFlow: Flow<DailySummary> = context.dailySummaryDataStore.data
        .map { prefs ->
            val today = todayString()
            val storedDate = prefs[KEY_DATE] ?: today
            val focusSeconds = prefs[KEY_FOCUS_SECONDS] ?: 0L
            val completed = prefs[KEY_COMPLETED_SESSIONS] ?: 0

            if (storedDate != today) {
                // Dia virou: zera o resumo
                DailySummary(
                    date = today,
                    focusSeconds = 0L,
                    completedFocusSessions = 0
                )
            } else {
                DailySummary(
                    date = storedDate,
                    focusSeconds = focusSeconds,
                    completedFocusSessions = completed
                )
            }
        }


    suspend fun addFocusProgress(
        additionalSeconds: Long,
        additionalCompletedSessions: Int
    ) {
        context.dailySummaryDataStore.edit { prefs ->
            val today = todayString()
            val storedDate = prefs[KEY_DATE]

            val baseFocusSeconds: Long
            val baseCompleted: Int

            if (storedDate == null || storedDate != today) {
                prefs[KEY_DATE] = today
                baseFocusSeconds = 0L
                baseCompleted = 0
            } else {
                baseFocusSeconds = prefs[KEY_FOCUS_SECONDS] ?: 0L
                baseCompleted = prefs[KEY_COMPLETED_SESSIONS] ?: 0
            }

            val newSeconds = baseFocusSeconds + additionalSeconds
            val newCompleted = baseCompleted + additionalCompletedSessions

            prefs[KEY_DATE] = today
            prefs[KEY_FOCUS_SECONDS] = newSeconds
            prefs[KEY_COMPLETED_SESSIONS] = newCompleted
        }
    }

    suspend fun resetToday() {
        context.dailySummaryDataStore.edit { prefs ->
            val today = todayString()
            prefs[KEY_DATE] = today
            prefs[KEY_FOCUS_SECONDS] = 0L
            prefs[KEY_COMPLETED_SESSIONS] = 0
        }
    }
}
