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
            )
            channel.description = "Exibe o timer Pomodoro em primeiro plano"
            channel.setSound(null, null)
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

        val openAppIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val playPauseAction = if (isRunning) {
            createAction(context, PomodoroService.ACTION_PAUSE, "Pausar", R.drawable.ic_stat_timer)
        } else {
            createAction(context, PomodoroService.ACTION_START, "Iniciar", R.drawable.ic_stat_timer)
        }

        val resetAction = createAction(context, PomodoroService.ACTION_RESET, "Resetar", R.drawable.ic_stat_timer)

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(formattedTime)
            .setSmallIcon(R.drawable.ic_stat_focusflow)
            .setOngoing(true)
            .setShowWhen(false)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setContentIntent(openAppIntent)
            .addAction(playPauseAction)
            .addAction(resetAction)
            .build()
    }

    private fun createAction(context: Context, action: String, title: String, icon: Int): NotificationCompat.Action {
        val intent = Intent(context, PomodoroService::class.java).apply { this.action = action }
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
