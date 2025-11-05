package com.dice.focusflow.feature.pomodoro.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.dice.focusflow.MainActivity
import com.dice.focusflow.R
import com.dice.focusflow.feature.pomodoro.PomodoroPhase

object NotificationHelper {

    const val NOTIF_ID = 1
    private const val CHANNEL_ID = "FocusFlowChannel"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "FocusFlow Timer",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Exibe o timer Pomodoro em primeiro plano"
                setSound(null, null)
            }

            NotificationManagerCompat.from(context).createNotificationChannel(channel)
        }
    }

    fun buildNotification(
        context: Context,
        phase: PomodoroPhase,
        remainingSeconds: Int,
        isRunning: Boolean
    ): Notification {
        val title = phaseTitle(phase)
        val formattedTime = formatMMSS(remainingSeconds)

        val openMainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            0,
            openMainIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val playPauseAction = if (isRunning) {
            createAction(
                context = context,
                action = PomodoroService.ACTION_PAUSE,
                title = "Pausar",
                icon = R.drawable.ic_stat_timer
            )
        } else {
            createAction(
                context = context,
                action = PomodoroService.ACTION_START,
                title = "Iniciar",
                icon = R.drawable.ic_stat_timer
            )
        }

        val resetAction = createAction(
            context = context,
            action = PomodoroService.ACTION_RESET,
            title = "Resetar",
            icon = R.drawable.ic_stat_timer
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(formattedTime)
            .setSmallIcon(R.drawable.ic_stat_focusflow)
            .setOngoing(true)
            .setShowWhen(false)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setContentIntent(openAppPendingIntent)
            .addAction(playPauseAction)
            .addAction(resetAction)
            .build()
    }

    private fun createAction(
        context: Context,
        action: String,
        title: String,
        icon: Int
    ): NotificationCompat.Action {
        val intent = Intent(context, PomodoroService::class.java).apply {
            this.action = action
        }

        val pendingIntent = PendingIntent.getService(
            context,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Action(icon, title, pendingIntent)
    }

    private fun phaseTitle(phase: PomodoroPhase): String = when (phase) {
        PomodoroPhase.Focus -> "Foco"
        PomodoroPhase.ShortBreak -> "Pausa curta"
        PomodoroPhase.LongBreak -> "Pausa longa"
    }

    private fun formatMMSS(totalSeconds: Int): String {
        val s = totalSeconds.coerceAtLeast(0)
        val mm = s / 60
        val ss = s % 60
        return "%02d:%02d".format(mm, ss)
    }
}
