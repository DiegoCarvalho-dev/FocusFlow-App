package com.dice.focusflow.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.Home.route,
        modifier = modifier
    ) {
        composable(AppRoute.Home.route) { HomeScreen() }
        composable(AppRoute.Tasks.route) { TasksScreen() }
        composable(AppRoute.Summary.route) { SummaryScreen() }
        composable(AppRoute.Settings.route) { SettingsScreen() }
    }
}
