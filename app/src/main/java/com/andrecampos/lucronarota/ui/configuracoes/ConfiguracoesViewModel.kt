package com.andrecampos.lucronarota.ui.configuracoes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrecampos.lucronarota.data.ConfigVeiculo
import com.andrecampos.lucronarota.data.CorridaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ConfiguracoesUiState(
    val precoCombustivel: String = "",
    val consumoKmPorLitro: String = "",
    val outrosCustosPorKm: String = "",
    val custoFixoDiario: String = "",
    val salvoComSucesso: Boolean = false
)

class ConfiguracoesViewModel(private val repository: CorridaRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ConfiguracoesUiState())
    val uiState: StateFlow<ConfiguracoesUiState> = _uiState.asStateFlow()

    private var carregouInicial = false

    init {
        viewModelScope.launch {
            repository.observarConfig().collect { config ->
                if (!carregouInicial) {
                    val atual = config ?: ConfigVeiculo()
                    _uiState.value = ConfiguracoesUiState(
                        precoCombustivel = atual.precoCombustivel.toString(),
                        consumoKmPorLitro = atual.consumoKmPorLitro.toString(),
                        outrosCustosPorKm = atual.outrosCustosPorKm.toString(),
                        custoFixoDiario = atual.custoFixoDiario.toString()
                    )
                    carregouInicial = true
                }
            }
        }
    }

    fun atualizarPreco(valor: String) {
        _uiState.value = _uiState.value.copy(precoCombustivel = valor)
    }

    fun atualizarConsumo(valor: String) {
        _uiState.value = _uiState.value.copy(consumoKmPorLitro = valor)
    }

    fun atualizarOutrosCustos(valor: String) {
        _uiState.value = _uiState.value.copy(outrosCustosPorKm = valor)
    }

    fun atualizarCustoFixo(valor: String) {
        _uiState.value = _uiState.value.copy(custoFixoDiario = valor)
    }

    fun salvar() {
        val estado = _uiState.value
        viewModelScope.launch {
            repository.salvarConfig(
                ConfigVeiculo(
                    precoCombustivel = estado.precoCombustivel.replace(",", ".").toDoubleOrNull() ?: 0.0,
                    consumoKmPorLitro = estado.consumoKmPorLitro.replace(",", ".").toDoubleOrNull() ?: 1.0,
                    outrosCustosPorKm = estado.outrosCustosPorKm.replace(",", ".").toDoubleOrNull() ?: 0.0,
                    custoFixoDiario = estado.custoFixoDiario.replace(",", ".").toDoubleOrNull() ?: 0.0
                )
            )
            _uiState.value = _uiState.value.copy(salvoComSucesso = true)
        }
    }

    fun confirmarSalvo() {
        _uiState.value = _uiState.value.copy(salvoComSucesso = false)
    }
}
