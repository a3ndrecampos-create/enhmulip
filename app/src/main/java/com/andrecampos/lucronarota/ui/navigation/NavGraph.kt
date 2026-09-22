package com.andrecampos.lucronarota.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.andrecampos.lucronarota.ui.ViewModelFactory
import com.andrecampos.lucronarota.ui.configuracoes.ConfiguracoesScreen
import com.andrecampos.lucronarota.ui.dashboard.DashboardScreen
import com.andrecampos.lucronarota.ui.historico.HistoricoScreen
import com.andrecampos.lucronarota.ui.novacorrida.NovaCorridaScreen

@Composable
fun LucroNaRotaApp(factory: ViewModelFactory) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val rotaAtual = navBackStackEntry?.destination?.route

                Screen.itensMenu.forEach { item ->
                    NavigationBarItem(
                        selected = rotaAtual == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icone, contentDescription = item.titulo) },
                        label = { Text(item.titulo) }
                    )
                }
            }
        }
    ) { paddingInterno ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(paddingInterno)
        ) {
            composable(Screen.Dashboard.route) { DashboardScreen(factory) }
            composable(Screen.NovaCorrida.route) { NovaCorridaScreen(factory) }
            composable(Screen.Historico.route) { HistoricoScreen(factory) }
            composable(Screen.Configuracoes.route) { ConfiguracoesScreen(factory) }
        }
    }
}
