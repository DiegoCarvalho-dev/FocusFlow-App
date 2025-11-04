package com.dice.focusflow.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dice.focusflow.feature.settings.AppThemeMode
import com.dice.focusflow.feature.settings.SettingsUiState
import com.dice.focusflow.feature.settings.SettingsViewModel

@Composable
fun SettingsScreen(
    vm: SettingsViewModel = viewModel()
) {
    val state by vm.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        Text(
            text = "Configurações",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            )
        )

        DurationSection(
            state = state,
            onFocusChange = { vm.updateFocusMinutes(it) },
            onShortBreakChange = { vm.updateShortBreakMinutes(it) },
            onLongBreakChange = { vm.updateLongBreakMinutes(it) }
        )

        Divider()

        ThemeSection(
            themeMode = state.themeMode,
            onThemeChange = { vm.updateThemeMode(it) }
        )

        Divider()

        SoundVibrationSection(
            soundEnabled = state.soundEnabled,
            vibrationEnabled = state.vibrationEnabled,
            onSoundChange = { vm.updateSoundEnabled(it) },
            onVibrationChange = { vm.updateVibrationEnabled(it) }
        )
    }
}

@Composable
private fun DurationSection(
    state: SettingsUiState,
    onFocusChange: (Int) -> Unit,
    onShortBreakChange: (Int) -> Unit,
    onLongBreakChange: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Durações do Pomodoro",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )

        Text(
            text = "Ajuste os tempos de foco e pausas. Os valores são salvos automaticamente.",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(Modifier.height(8.dp))

        DurationSliderRow(
            label = "Foco",
            minutes = state.focusMinutes,
            range = 10f..60f,
            onChange = onFocusChange
        )

        DurationSliderRow(
            label = "Pausa curta",
            minutes = state.shortBreakMinutes,
            range = 1f..20f,
            onChange = onShortBreakChange
        )

        DurationSliderRow(
            label = "Pausa longa",
            minutes = state.longBreakMinutes,
            range = 5f..40f,
            onChange = onLongBreakChange
        )
    }
}

@Composable
private fun DurationSliderRow(
    label: String,
    minutes: Int,
    range: ClosedFloatingPointRange<Float>,
    onChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text("${minutes} min", style = MaterialTheme.typography.bodyMedium)
        }

        Slider(
            value = minutes.toFloat(),
            onValueChange = { onChange(it.toInt()) },
            valueRange = range,
            steps = 0,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
private fun ThemeSection(
    themeMode: AppThemeMode,
    onThemeChange: (AppThemeMode) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Tema",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )

        Text(
            text = "Escolha se o app usa tema claro, escuro ou segue o sistema.",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThemeChip(
                label = "Sistema",
                selected = themeMode == AppThemeMode.SYSTEM,
                onClick = { onThemeChange(AppThemeMode.SYSTEM) }
            )
            ThemeChip(
                label = "Claro",
                selected = themeMode == AppThemeMode.LIGHT,
                onClick = { onThemeChange(AppThemeMode.LIGHT) }
            )
            ThemeChip(
                label = "Escuro",
                selected = themeMode == AppThemeMode.DARK,
                onClick = { onThemeChange(AppThemeMode.DARK) }
            )
        }
    }
}

@Composable
private fun ThemeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = { Text(label) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
            labelColor = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    )
}

@Composable
private fun SoundVibrationSection(
    soundEnabled: Boolean,
    vibrationEnabled: Boolean,
    onSoundChange: (Boolean) -> Unit,
    onVibrationChange: (Boolean) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Som e vibração",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )

        Text(
            text = "Controle se o app pode emitir som e vibração.",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(Modifier.height(8.dp))

        SettingsSwitchRow(
            label = "Som",
            checked = soundEnabled,
            onCheckedChange = onSoundChange
        )

        SettingsSwitchRow(
            label = "Vibração",
            checked = vibrationEnabled,
            onCheckedChange = onVibrationChange
        )
    }
}

@Composable
private fun SettingsSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
