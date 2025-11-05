package com.dice.focusflow.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dice.focusflow.feature.settings.SettingsViewModel
import com.dice.focusflow.feature.tasks.TasksViewModel
import com.dice.focusflow.ui.screens.HomeScreen
import com.dice.focusflow.ui.screens.SettingsScreen
import com.dice.focusflow.ui.screens.SummaryScreen
import com.dice.focusflow.ui.screens.TasksScreen

enum class AppRoute(val route: String) {
    Home("home"),
    Tasks("tasks"),
    Summary("summary"),
    Settings("settings")
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    settingsVm: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val tasksVm: TasksViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = AppRoute.Home.route,
        modifier = modifier
    ) {
        composable(AppRoute.Home.route) {
            HomeScreen(settingsVm = settingsVm)
        }
        composable(AppRoute.Tasks.route) {
            TasksScreen(vm = tasksVm)
        }
        composable(AppRoute.Summary.route) {
            SummaryScreen(settingsVm = settingsVm)
        }
        composable(AppRoute.Settings.route) {
            SettingsScreen(vm = settingsVm)
        }
    }
}
