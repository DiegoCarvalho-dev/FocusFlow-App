package com.dice.focusflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.dice.focusflow.feature.settings.SettingsViewModel
import com.dice.focusflow.ui.components.BottomBar
import com.dice.focusflow.ui.navigation.AppNavGraph
import com.dice.focusflow.ui.theme.FocusFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FocusFlowApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusFlowApp() {
    val navController = rememberNavController()

    val settingsVm: SettingsViewModel = viewModel()

    FocusFlowTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("FocusFlow") }
                )
            },
            bottomBar = {
                BottomBar(navController = navController)
            }
        ) { innerPadding ->
            AppNavGraph(
                navController = navController,
                settingsVm = settingsVm,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFocusFlow() {
    FocusFlowApp()
}
