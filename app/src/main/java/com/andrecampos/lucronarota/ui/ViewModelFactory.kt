package com.andrecampos.lucronarota.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.andrecampos.lucronarota.data.DiariaRepository
import com.andrecampos.lucronarota.ui.dashboard.DashboardViewModel
import com.andrecampos.lucronarota.ui.diaria.DiariaViewModel
import com.andrecampos.lucronarota.ui.historico.HistoricoViewModel
import com.andrecampos.lucronarota.ui.veiculos.VeiculosViewModel

class ViewModelFactory(private val repository: DiariaRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(DashboardViewModel::class.java) ->
                DashboardViewModel(repository) as T
            modelClass.isAssignableFrom(DiariaViewModel::class.java) ->
                DiariaViewModel(repository) as T
            modelClass.isAssignableFrom(HistoricoViewModel::class.java) ->
                HistoricoViewModel(repository) as T
            modelClass.isAssignableFrom(VeiculosViewModel::class.java) ->
                VeiculosViewModel(repository) as T
            else -> throw IllegalArgumentException("ViewModel desconhecida: ${modelClass.name}")
        }
    }
}
