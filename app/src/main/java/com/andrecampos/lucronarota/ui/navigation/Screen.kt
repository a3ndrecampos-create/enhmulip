package com.andrecampos.lucronarota.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val titulo: String, val icone: ImageVector) {
    data object Dashboard : Screen("dashboard", "Início", Icons.Filled.Home)
    data object NovaCorrida : Screen("nova_corrida", "Nova corrida", Icons.Filled.Add)
    data object Historico : Screen("historico", "Histórico", Icons.Filled.History)
    data object Configuracoes : Screen("configuracoes", "Ajustes", Icons.Filled.Settings)

    companion object {
        val itensMenu = listOf(Dashboard, NovaCorrida, Historico, Configuracoes)
    }
}
