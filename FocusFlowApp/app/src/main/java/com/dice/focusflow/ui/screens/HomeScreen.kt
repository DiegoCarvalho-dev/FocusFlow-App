package com.dice.focusflow.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dice.focusflow.feature.pomodoro.EngineLocator
import com.dice.focusflow.feature.pomodoro.PomodoroConfig
import com.dice.focusflow.feature.pomodoro.PomodoroPhase
import com.dice.focusflow.feature.pomodoro.PomodoroViewModel
import com.dice.focusflow.feature.pomodoro.PomodoroViewModelFactory
import com.dice.focusflow.feature.pomodoro.engine.PomodoroEngineImpl
import com.dice.focusflow.feature.pomodoro.service.PomodoroService
import com.dice.focusflow.feature.settings.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Composable
fun HomeScreen(
    settingsVm: SettingsViewModel
) {
    val context = LocalContext.current

    val settingsState by settingsVm.uiState.collectAsStateWithLifecycle()

    val engine = remember {
        val existing = EngineLocator.current()
        if (existing != null) {
            existing
        } else {
            PomodoroEngineImpl(
                config = PomodoroConfig(
                    focusMinutes = settingsState.focusMinutes,
                    shortBreakMinutes = settingsState.shortBreakMinutes,
                    longBreakMinutes = settingsState.longBreakMinutes
                ),
                scope = CoroutineScope(Dispatchers.Default)
            ).also { created ->
                EngineLocator.install(created)
            }
        }
    }

    LaunchedEffect(
        settingsState.focusMinutes,
        settingsState.shortBreakMinutes,
        settingsState.longBreakMinutes
    ) {
        engine.setConfig(
            PomodoroConfig(
                focusMinutes = settingsState.focusMinutes,
                shortBreakMinutes = settingsState.shortBreakMinutes,
                longBreakMinutes = settingsState.longBreakMinutes
            )
        )
    }

    LaunchedEffect(engine) {
        EngineLocator.install(engine)
    }

    val vm: PomodoroViewModel = viewModel(
        factory = PomodoroViewModelFactory(engine = engine)
    )
    val state by vm.state.collectAsStateWithLifecycle()

    val totalSecondsForPhase = when (state.phase) {
        PomodoroPhase.Focus -> settingsState.focusMinutes * 60
        PomodoroPhase.ShortBreak -> settingsState.shortBreakMinutes * 60
        PomodoroPhase.LongBreak -> settingsState.longBreakMinutes * 60
    }.coerceAtLeast(1)

    val elapsedSeconds =
        (totalSecondsForPhase - state.remainingSeconds).coerceIn(0, totalSecondsForPhase)

    val targetProgress = (elapsedSeconds.toFloat() / totalSecondsForPhase.toFloat())
        .coerceIn(0f, 1f)

    val progress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = spring(dampingRatio = 0.9f, stiffness = 200f),
        label = "timerProgress"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedContent(
            targetState = state.phase,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "phaseTitle"
        ) { phase ->
            Text(
                text = phaseTitle(phase),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Text(
            text = if (state.isRunning) "Ciclo em andamento" else "Timer pausado",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            val phaseColor = when (state.phase) {
                PomodoroPhase.Focus -> MaterialTheme.colorScheme.primary
                PomodoroPhase.ShortBreak -> MaterialTheme.colorScheme.tertiary
                PomodoroPhase.LongBreak -> MaterialTheme.colorScheme.secondary
            }

            CircularProgressIndicator(
                progress = { progress },
                strokeCap = StrokeCap.Round,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                color = phaseColor,
                modifier = Modifier.size(220.dp),
                strokeWidth = 12.dp
            )

            AnimatedContent(
                targetState = state.remainingSeconds,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "timeSwap"
            ) { seconds ->
                Text(
                    text = formatMMSS(seconds),
                    fontSize = 44.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        AssistChip(
            onClick = { /* no-op */ },
            label = { Text(phaseTitle(state.phase)) },
            colors = AssistChipDefaults.assistChipColors(
                labelColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = when (state.phase) {
                    PomodoroPhase.Focus -> MaterialTheme.colorScheme.primary
                    PomodoroPhase.ShortBreak -> MaterialTheme.colorScheme.tertiary
                    PomodoroPhase.LongBreak -> MaterialTheme.colorScheme.secondary
                }
            )
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                val intent = Intent(context, PomodoroService::class.java)

                if (state.isRunning) {
                    vm.pause()
                    intent.action = PomodoroService.ACTION_PAUSE
                } else {
                    vm.start()
                    intent.action = PomodoroService.ACTION_START
                }

                context.startService(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            AnimatedContent(
                targetState = state.isRunning,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "playPauseText"
            ) { running ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (running) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (running) {
                            "Pausar timer"
                        } else {
                            "Iniciar timer"
                        }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(if (running) "Pausar" else "Iniciar")
                }
            }
        }

        OutlinedButton(
            onClick = {
                vm.resetToFocus()
                context.stopService(Intent(context, PomodoroService::class.java))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Resetar")
        }

        AssistChip(
            onClick = { /* no-op */ },
            label = {
                Text(
                    if (state.isRunning) "Rodando" else "Pausado"
                )
            }
        )
    }
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
