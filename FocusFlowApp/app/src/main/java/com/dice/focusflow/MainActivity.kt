package com.dice.focusflow

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.dice.focusflow.ui.components.BottomBar
import com.dice.focusflow.ui.navigation.AppNavGraph
import com.dice.focusflow.ui.theme.FocusFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { FocusFlowApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusFlowApp() {
    LaunchedEffect(Unit) {
    }
    val requestNotif = if (Build.VERSION.SDK_INT >= 33) {
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { /* granted -> ok; negado -> app segue sem notif */ }
    } else null

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= 33) {
            requestNotif?.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val navController = rememberNavController()

    FocusFlowTheme {
        Scaffold(
            topBar = { CenterAlignedTopAppBar(title = { Text("FocusFlow") }) },
            bottomBar = { BottomBar(navController = navController) }
        ) { innerPadding ->
            AppNavGraph(
                navController = navController,
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
