package com.dice.focusflow.feature.pomodoro.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import com.dice.focusflow.feature.pomodoro.EngineLocator
import com.dice.focusflow.feature.pomodoro.engine.PomodoroEngine
import com.dice.focusflow.feature.pomodoro.PomodoroPhase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

class PomodoroService : Service() {

    companion object {
        const val ACTION_START = "com.dice.focusflow.ACTION_START"
        const val ACTION_PAUSE = "com.dice.focusflow.ACTION_PAUSE"
        const val ACTION_RESET = "com.dice.focusflow.ACTION_RESET"
    }

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(serviceJob + Dispatchers.Main.immediate)
    private var streamJob: Job? = null

    private val engine: PomodoroEngine?
        get() = EngineLocator.current()

    override fun onCreate() {
        super.onCreate()

        NotificationHelper.ensureChannel(this)

        val initialState = engine?.state?.value

        val initialNotification = NotificationHelper.buildNotification(
            context = this,
            phase = initialState?.phase ?: PomodoroPhase.Focus,
            remainingSeconds = initialState?.remainingSeconds ?: 25 * 60,
            isRunning = initialState?.isRunning ?: false
        )

        startForeground(NotificationHelper.NOTIF_ID, initialNotification)
        observeEngine()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> engine?.start()
            ACTION_PAUSE -> engine?.pause()
            ACTION_RESET -> engine?.resetToFocus()
        }
        return START_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun observeEngine() {
        streamJob?.cancel()
        val eng = engine ?: return

        streamJob = serviceScope.launch {
            eng.state.collectLatest { s ->
                val notif = NotificationHelper.buildNotification(
                    context = this@PomodoroService,
                    phase = s.phase,
                    remainingSeconds = s.remainingSeconds,
                    isRunning = s.isRunning
                )
                NotificationManagerCompat.from(this@PomodoroService)
                    .notify(NotificationHelper.NOTIF_ID, notif)
            }
        }
    }

    override fun onDestroy() {
        streamJob?.cancel()
        serviceJob.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
