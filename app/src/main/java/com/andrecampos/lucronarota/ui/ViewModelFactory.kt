package com.andrecampos.lucronarota.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.andrecampos.lucronarota.data.CorridaRepository
import com.andrecampos.lucronarota.ui.configuracoes.ConfiguracoesViewModel
import com.andrecampos.lucronarota.ui.dashboard.DashboardViewModel
import com.andrecampos.lucronarota.ui.historico.HistoricoViewModel
import com.andrecampos.lucronarota.ui.novacorrida.NovaCorridaViewModel

class ViewModelFactory(private val repository: CorridaRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(DashboardViewModel::class.java) ->
                DashboardViewModel(repository) as T
            modelClass.isAssignableFrom(NovaCorridaViewModel::class.java) ->
                NovaCorridaViewModel(repository) as T
            modelClass.isAssignableFrom(HistoricoViewModel::class.java) ->
                HistoricoViewModel(repository) as T
            modelClass.isAssignableFrom(ConfiguracoesViewModel::class.java) ->
                ConfiguracoesViewModel(repository) as T
            else -> throw IllegalArgumentException("ViewModel desconhecida: ${modelClass.name}")
        }
    }
}
