package com.dice.focusflow.feature.pomodoro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dice.focusflow.feature.pomodoro.engine.PomodoroEngine

class PomodoroViewModelFactory(
    private val engine: PomodoroEngine
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PomodoroViewModel::class.java)) {
            return PomodoroViewModel(engine) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
