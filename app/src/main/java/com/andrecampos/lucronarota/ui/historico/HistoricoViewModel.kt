package com.andrecampos.lucronarota.ui.historico

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrecampos.lucronarota.data.Diaria
import com.andrecampos.lucronarota.data.DiariaRepository
import com.andrecampos.lucronarota.data.Veiculo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class HistoricoUiState(
    val diarias: List<Diaria> = emptyList(),
    val veiculos: List<Veiculo> = emptyList(),
    val filtroVeiculoId: Long? = null
)

class HistoricoViewModel(private val repository: DiariaRepository) : ViewModel() {

    private val filtroSelecionado = MutableStateFlow<Long?>(null)

    private val _uiState = MutableStateFlow(HistoricoUiState())
    val uiState: StateFlow<HistoricoUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.observarFinalizadas(),
                repository.observarVeiculos(),
                filtroSelecionado
            ) { diarias, veiculos, filtro ->
                val filtradas = if (filtro == null) diarias else diarias.filter { it.veiculoId == filtro }
                HistoricoUiState(filtradas, veiculos, filtro)
            }.collect { _uiState.value = it }
        }
    }

    fun selecionarFiltro(veiculoId: Long?) {
        filtroSelecionado.value = veiculoId
    }

    fun remover(diaria: Diaria) {
        viewModelScope.launch { repository.removerDiaria(diaria) }
    }
}
