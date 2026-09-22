package com.andrecampos.lucronarota.ui.historico

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrecampos.lucronarota.data.ConfigVeiculo
import com.andrecampos.lucronarota.data.Corrida
import com.andrecampos.lucronarota.data.CorridaRepository
import com.andrecampos.lucronarota.data.TipoCorrida
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

enum class FiltroTipo { TODAS, APP, PARTICULAR }

data class HistoricoUiState(
    val corridas: List<Corrida> = emptyList(),
    val config: ConfigVeiculo = ConfigVeiculo(),
    val filtro: FiltroTipo = FiltroTipo.TODAS
)

class HistoricoViewModel(private val repository: CorridaRepository) : ViewModel() {

    private val filtroSelecionado = MutableStateFlow(FiltroTipo.TODAS)

    private val _uiState = MutableStateFlow(HistoricoUiState())
    val uiState: StateFlow<HistoricoUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.observarCorridas(),
                repository.observarConfig(),
                filtroSelecionado
            ) { corridas, config, filtro ->
                val filtradas = when (filtro) {
                    FiltroTipo.TODAS -> corridas
                    FiltroTipo.APP -> corridas.filter { it.tipo == TipoCorrida.APP }
                    FiltroTipo.PARTICULAR -> corridas.filter { it.tipo == TipoCorrida.PARTICULAR }
                }
                HistoricoUiState(filtradas, config ?: ConfigVeiculo(), filtro)
            }.collect { _uiState.value = it }
        }
    }

    fun selecionarFiltro(filtro: FiltroTipo) {
        filtroSelecionado.value = filtro
    }

    fun remover(corrida: Corrida) {
        viewModelScope.launch { repository.removerCorrida(corrida) }
    }
}
