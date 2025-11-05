package com.dice.focusflow.feature.pomodoro.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import com.dice.focusflow.feature.pomodoro.EngineLocator
import com.dice.focusflow.feature.pomodoro.PomodoroPhase
import com.dice.focusflow.feature.pomodoro.engine.PomodoroEngine
import com.dice.focusflow.feature.summary.DailySummaryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PomodoroService : Service() {

    companion object {
        const val ACTION_START = "com.dice.focusflow.POMODORO_START"
        const val ACTION_PAUSE = "com.dice.focusflow.POMODORO_PAUSE"
        const val ACTION_RESET = "com.dice.focusflow.POMODORO_RESET"
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var streamJob: Job? = null

    private var engine: PomodoroEngine? = null

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.ensureChannel(this)
    }

    @SuppressLint("MissingPermission")
    private fun observeEngine() {
        val eng = this.engine ?: return

        val summaryRepo = DailySummaryRepository(applicationContext)

        streamJob?.cancel()

        streamJob = serviceScope.launch {
            var lastPhase: PomodoroPhase? = null
            var lastRemaining: Int? = null
            var lastIsRunning: Boolean = false

            eng.state.collectLatest { s ->
                val notif = NotificationHelper.buildNotification(
                    context = this@PomodoroService,
                    phase = s.phase,
                    remainingSeconds = s.remainingSeconds,
                    isRunning = s.isRunning
                )

                NotificationManagerCompat.from(this@PomodoroService)
                    .notify(NotificationHelper.NOTIF_ID, notif)

                val previousPhase = lastPhase
                val previousRemaining = lastRemaining
                val previousRunning = lastIsRunning

                if (
                    s.phase == PomodoroPhase.Focus &&
                    s.isRunning &&
                    previousPhase == PomodoroPhase.Focus &&
                    previousRunning
                ) {
                    if (previousRemaining != null) {
                        val delta = (previousRemaining - s.remainingSeconds).coerceAtLeast(0)
                        if (delta > 0) {
                            launch {
                                summaryRepo.addFocusProgress(
                                    additionalSeconds = delta.toLong(),
                                    additionalCompletedSessions = 0
                                )
                            }
                        }
                    }
                }

                if (
                    previousPhase == PomodoroPhase.Focus &&
                    (s.phase == PomodoroPhase.ShortBreak || s.phase == PomodoroPhase.LongBreak)
                ) {
                    val missingSeconds = (previousRemaining ?: 0).coerceAtLeast(0)

                    launch {
                        summaryRepo.addFocusProgress(
                            additionalSeconds = missingSeconds.toLong(),
                            additionalCompletedSessions = 1
                        )
                    }
                }

                lastPhase = s.phase
                lastRemaining = s.remainingSeconds
                lastIsRunning = s.isRunning
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val currentEngine = EngineLocator.current()

        if (currentEngine == null) {
            stopSelf()
            return START_NOT_STICKY
        }

        if (this.engine !== currentEngine) {
            this.engine = currentEngine
            observeEngine()
        }

        if (!isForegroundServiceRunning()) {
            val initialState = currentEngine.state.value
            val initialNotification = NotificationHelper.buildNotification(
                context = this,
                phase = initialState.phase,
                remainingSeconds = initialState.remainingSeconds,
                isRunning = initialState.isRunning
            )
            startForeground(NotificationHelper.NOTIF_ID, initialNotification)
        }

        when (intent?.action) {
            ACTION_START -> this.engine?.start()
            ACTION_PAUSE -> this.engine?.pause()
            ACTION_RESET -> this.engine?.resetToFocus()
        }

        return START_STICKY
    }

    private fun isForegroundServiceRunning(): Boolean {
        return this.engine != null
    }

    override fun onDestroy() {
        streamJob?.cancel()
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
