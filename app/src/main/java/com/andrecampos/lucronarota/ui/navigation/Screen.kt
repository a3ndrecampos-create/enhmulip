package com.andrecampos.lucronarota.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val titulo: String, val icone: ImageVector) {
    data object Dashboard : Screen("dashboard", "Início", Icons.Filled.Home)
    data object Diaria : Screen("diaria", "Diária", Icons.Filled.PlayCircle)
    data object Historico : Screen("historico", "Histórico", Icons.Filled.History)
    data object Veiculos : Screen("veiculos", "Veículos", Icons.Filled.DirectionsCar)

    companion object {
        val itensMenu = listOf(Dashboard, Diaria, Historico, Veiculos)
    }
}
