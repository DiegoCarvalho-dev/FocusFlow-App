package com.dice.focusflow.feature.pomodoro

import com.dice.focusflow.feature.pomodoro.engine.PomodoroEngineImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PomodoroEngineImplTest {

    private fun createEngine(): PomodoroEngineImpl {
        val scope = CoroutineScope(Job() + Dispatchers.Unconfined)

        val config = PomodoroConfig(
            focusMinutes = 1,
            shortBreakMinutes = 1,
            longBreakMinutes = 2
        )

        return PomodoroEngineImpl(config, scope)
    }

    @Test
    fun startAndPauseChangeIsRunningFlag() {
        val engine = createEngine()

        assertFalse(engine.state.value.isRunning)
        engine.start()
        assertTrue(engine.state.value.isRunning)
        engine.pause()
        assertFalse(engine.state.value.isRunning)
    }

    @Test
    fun resetReturnsToFocusStopped() {
        val engine = createEngine()
        engine.start()
        engine.resetToFocus()
        val state = engine.state.value
        assertEquals(PomodoroPhase.Focus, state.phase)
        assertFalse(state.isRunning)
    }

    @Test
    fun skipPhaseMovesFromFocusToShortBreak() {
        val engine = createEngine()
        assertEquals(PomodoroPhase.Focus, engine.state.value.phase)
        engine.skipPhase()
        assertEquals(PomodoroPhase.ShortBreak, engine.state.value.phase)
    }
}
