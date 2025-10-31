package com.dice.focusflow.ui.components

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.dice.focusflow.ui.navigation.AppRoute

@Composable
fun BottomBar(navController: NavController) {
    val items = listOf(
        AppRoute.Home,
        AppRoute.Tasks,
        AppRoute.Summary,
        AppRoute.Settings
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { route ->
            val selected = currentRoute == route.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(route.route) {
                            popUpTo(AppRoute.Home.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },
                icon = { /* Ícones serão adicionados depois */ },
                label = { Text(routeLabel(route)) }
            )
        }
    }
}

private fun routeLabel(route: AppRoute): String = when (route) {
    AppRoute.Home -> "Home"
    AppRoute.Tasks -> "Tarefas"
    AppRoute.Summary -> "Resumo"
    AppRoute.Settings -> "Config."
}
