package com.dice.focusflow.feature.pomodoro

object EngineLocator {

    @Volatile
    private var _engine: com.dice.focusflow.feature.pomodoro.engine.PomodoroEngine? = null

    fun current(): com.dice.focusflow.feature.pomodoro.engine.PomodoroEngine? = _engine

    fun install(engine: com.dice.focusflow.feature.pomodoro.engine.PomodoroEngine) {
        _engine = engine
    }
}
