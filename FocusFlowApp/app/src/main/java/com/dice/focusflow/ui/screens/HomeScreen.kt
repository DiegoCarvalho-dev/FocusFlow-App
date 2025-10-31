package com.dice.focusflow.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dice.focusflow.feature.pomodoro.PomodoroConfig
import com.dice.focusflow.feature.pomodoro.PomodoroPhase
import com.dice.focusflow.feature.pomodoro.PomodoroViewModel

@Composable
fun HomeScreen(
    VM: PomodoroViewModel = viewModel()
) {
    val state by VM.state.collectAsState()
    val progress = VM.progressFraction()

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top)
        ) {
            Text(
                text = "FocusFlow",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold)
            )

            Text(
                text = phaseLabel(state.phase),
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = formatSeconds(state.remainingSeconds),
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold)
            )

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxSize(fraction = 0.95f)
                    .padding(top = 8.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!state.isRunning) {
                    Button(onClick = { VM.start() }, contentPadding = PaddingValues(horizontal = 28.dp, vertical = 10.dp)) {
                        Text("Iniciar")
                    }
                } else {
                    FilledTonalButton(onClick = { VM.pause() }) { Text("Pausar") }
                }

                ElevatedButton(onClick = { VM.resetToFocus() }) { Text("Resetar p/ Foco") }
                OutlinedButton(onClick = { VM.skipPhase() }) { Text("Pular fase") }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Presets rápidos", style = MaterialTheme.typography.titleSmall)
                PresetRow(
                    onSelect = { cfg -> VM.setConfig(cfg) }
                )
            }

            Text(
                text = "Pomodoros concluídos: ${state.completedPomodoros}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun PresetRow(onSelect: (PomodoroConfig) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedButton(onClick = { onSelect(PomodoroConfig()) }) { Text("25 / 5 / 15 (x4)") }
        OutlinedButton(onClick = { onSelect(PomodoroConfig(50, 10, 20, 3)) }) { Text("50 / 10 / 20 (x3)") }
        OutlinedButton(onClick = { onSelect(PomodoroConfig(15, 3, 8, 4)) }) { Text("15 / 3 / 8 (x4)") }
    }
}

private fun phaseLabel(phase: PomodoroPhase): String = when (phase) {
    PomodoroPhase.FOCUS -> "Foco"
    PomodoroPhase.SHORT_BREAK -> "Pausa curta"
    PomodoroPhase.LONG_BREAK -> "Pausa longa"
}

private fun formatSeconds(total: Int): String {
    val m = total / 60
    val s = total % 60
    return "%02d:%02d".format(m, s)
}
