package com.dice.focusflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dice.focusflow.feature.pomodoro.PomodoroPhase
import com.dice.focusflow.feature.pomodoro.PomodoroViewModel

@Composable
fun HomeScreen(
    vm: PomodoroViewModel = viewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = phaseTitle(state.phase),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )

        Text(
            text = formatMMSS(state.remainingSeconds),
            fontSize = 48.sp,
            fontWeight = FontWeight.SemiBold
        )

        LinearProgressIndicator(
            progress = progressOf(
                remainingSeconds = state.remainingSeconds,
                phase = state.phase
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        )

        Button(
            onClick = { if (state.isRunning) vm.pause() else vm.start() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(if (state.isRunning) "Pausar" else "Iniciar")
        }

        OutlinedButton(
            onClick = { vm.reset() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Resetar")
        }

        AssistChip(
            onClick = { /* no-op */ },
            label = { Text(if (state.isRunning) "Rodando" else "Pausado") }
        )
    }
}


private fun phaseTitle(phase: PomodoroPhase): String = when (phase) {
    PomodoroPhase.Focus -> "Foco"
    PomodoroPhase.ShortBreak -> "Pausa curta"
    PomodoroPhase.LongBreak -> "Pausa longa"
}


private fun phaseTotalSeconds(phase: PomodoroPhase): Int = when (phase) {
    PomodoroPhase.Focus -> 25 * 60
    PomodoroPhase.ShortBreak -> 5 * 60
    PomodoroPhase.LongBreak -> 15 * 60
}

private fun progressOf(remainingSeconds: Int, phase: PomodoroPhase): Float {
    val total = phaseTotalSeconds(phase).coerceAtLeast(1)
    val elapsed = (total - remainingSeconds).coerceIn(0, total)
    return (elapsed.toFloat() / total).coerceIn(0f, 1f)
}

private fun formatMMSS(totalSeconds: Int): String {
    val s = totalSeconds.coerceAtLeast(0)
    val mm = s / 60
    val ss = s % 60
    return "%02d:%02d".format(mm, ss)
}
