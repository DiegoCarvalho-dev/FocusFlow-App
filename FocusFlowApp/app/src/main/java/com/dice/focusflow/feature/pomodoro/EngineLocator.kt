package com.dice.focusflow.feature.pomodoro

import com.dice.focusflow.feature.pomodoro.engine.PomodoroEngine
object EngineLocator {

    @Volatile
    private var _engine: PomodoroEngine? = null

    fun install(engine: PomodoroEngine) {
        _engine = engine
    }
    fun current(): PomodoroEngine? = _engine
}
