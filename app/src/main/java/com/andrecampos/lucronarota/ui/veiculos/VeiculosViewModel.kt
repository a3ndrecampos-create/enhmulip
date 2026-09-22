package com.andrecampos.lucronarota.ui.veiculos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrecampos.lucronarota.data.DiariaRepository
import com.andrecampos.lucronarota.data.Veiculo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class VeiculosUiState(
    val veiculos: List<Veiculo> = emptyList(),
    val nome: String = "",
    val precoCombustivel: String = "6.10",
    val consumoKmPorLitro: String = "12.0",
    val outrosCustosPorKm: String = "0.15",
    val custoFixoDiario: String = "20.0",
    val salvoComSucesso: Boolean = false
) {
    val podeSalvar: Boolean get() = nome.isNotBlank()
}

class VeiculosViewModel(private val repository: DiariaRepository) : ViewModel() {

    private val formulario = MutableStateFlow(VeiculosUiState())

    private val _uiState = MutableStateFlow(VeiculosUiState())
    val uiState: StateFlow<VeiculosUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(repository.observarVeiculos(), formulario) { veiculos, form ->
                form.copy(veiculos = veiculos)
            }.collect { _uiState.value = it }
        }
    }

    fun atualizarNome(valor: String) {
        formulario.value = formulario.value.copy(nome = valor)
    }

    fun atualizarPreco(valor: String) {
        formulario.value = formulario.value.copy(precoCombustivel = valor)
    }

    fun atualizarConsumo(valor: String) {
        formulario.value = formulario.value.copy(consumoKmPorLitro = valor)
    }

    fun atualizarOutrosCustos(valor: String) {
        formulario.value = formulario.value.copy(outrosCustosPorKm = valor)
    }

    fun atualizarCustoFixo(valor: String) {
        formulario.value = formulario.value.copy(custoFixoDiario = valor)
    }

    fun salvar() {
        val estado = formulario.value
        if (!estado.podeSalvar) return

        viewModelScope.launch {
            repository.salvarVeiculo(
                Veiculo(
                    nome = estado.nome.trim(),
                    precoCombustivel = estado.precoCombustivel.replace(",", ".").toDoubleOrNull() ?: 6.10,
                    consumoKmPorLitro = estado.consumoKmPorLitro.replace(",", ".").toDoubleOrNull() ?: 12.0,
                    outrosCustosPorKm = estado.outrosCustosPorKm.replace(",", ".").toDoubleOrNull() ?: 0.15,
                    custoFixoDiario = estado.custoFixoDiario.replace(",", ".").toDoubleOrNull() ?: 20.0
                )
            )
            formulario.value = VeiculosUiState(salvoComSucesso = true)
        }
    }

    fun remover(veiculo: Veiculo) {
        viewModelScope.launch { repository.removerVeiculo(veiculo) }
    }

    fun confirmarSalvo() {
        formulario.value = formulario.value.copy(salvoComSucesso = false)
    }
}
