package com.dice.focusflow.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dice.focusflow.feature.pomodoro.EngineLocator
import com.dice.focusflow.feature.settings.SettingsViewModel

@Composable
fun SummaryScreen(
    settingsVm: SettingsViewModel
) {
    val settingsState by settingsVm.uiState.collectAsStateWithLifecycle()
    val focusMinutes = settingsState.focusMinutes

    val engine = EngineLocator.current()

    var showResetDialog by remember { mutableStateOf(false) }
    var offset by rememberSaveable { mutableStateOf(0) }

    val focusCyclesToday: Int
    val estimatedMinutes: Int

    if (engine != null) {
        val engineState by engine.state.collectAsStateWithLifecycle()
        val rawCycles = engineState.completedPomodoros
        val displayCycles = (rawCycles - offset).coerceAtLeast(0)
        focusCyclesToday = displayCycles
        estimatedMinutes = displayCycles * focusMinutes
    } else {
        focusCyclesToday = 0
        estimatedMinutes = 0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        Text(
            text = "Resumo do dia",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Text(
            text = "Veja quantos ciclos de foco você concluiu desde o último reset e o tempo estimado de foco.",
            style = MaterialTheme.typography.bodySmall
        )

        Divider()

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Ciclos de foco concluídos",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = "$focusCyclesToday",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Minutos focados (estimados)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = "$estimatedMinutes min",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(Modifier.height(16.dp))
        Divider()

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Zerar resumo",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = "Isso não apaga o histórico real do Pomodoro, só reinicia o contador mostrado aqui.",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { showResetDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Zerar resumo")
            }
        }

        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = {
                    Text("Zerar resumo")
                },
                text = {
                    Text(
                        "Isso não apaga o histórico real do Pomodoro, " +
                                "apenas reinicia o contador mostrado nesta tela."
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        val currentEngine = EngineLocator.current()
                        offset = if (currentEngine != null) {
                            currentEngine.state.value.completedPomodoros
                        } else {
                            0
                        }
                        showResetDialog = false
                    }) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
