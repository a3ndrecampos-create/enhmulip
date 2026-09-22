package com.andrecampos.lucronarota.ui.novacorrida

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrecampos.lucronarota.data.ConfigVeiculo
import com.andrecampos.lucronarota.data.Corrida
import com.andrecampos.lucronarota.data.CorridaRepository
import com.andrecampos.lucronarota.data.TipoCorrida
import com.andrecampos.lucronarota.util.Calculadora
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NovaCorridaUiState(
    val tipo: TipoCorrida = TipoCorrida.APP,
    val plataforma: String = "Uber",
    val valorGanho: String = "",
    val kmRodado: String = "",
    val duracaoMinutos: String = "",
    val observacao: String = "",
    val config: ConfigVeiculo = ConfigVeiculo(),
    val salvoComSucesso: Boolean = false
) {
    private val valorGanhoDouble: Double get() = valorGanho.replace(",", ".").toDoubleOrNull() ?: 0.0
    private val kmRodadoDouble: Double get() = kmRodado.replace(",", ".").toDoubleOrNull() ?: 0.0

    val custoEstimado: Double get() = kmRodadoDouble * Calculadora.custoPorKm(config)
    val lucroEstimado: Double get() = valorGanhoDouble - custoEstimado
    val formularioValido: Boolean get() = valorGanhoDouble > 0 && kmRodadoDouble > 0
}

class NovaCorridaViewModel(private val repository: CorridaRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(NovaCorridaUiState())
    val uiState: StateFlow<NovaCorridaUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observarConfig().collect { config ->
                _uiState.value = _uiState.value.copy(config = config ?: ConfigVeiculo())
            }
        }
    }

    fun atualizarTipo(tipo: TipoCorrida) {
        val plataformaPadrao = if (tipo == TipoCorrida.APP) "Uber" else "Particular"
        _uiState.value = _uiState.value.copy(tipo = tipo, plataforma = plataformaPadrao)
    }

    fun atualizarPlataforma(valor: String) {
        _uiState.value = _uiState.value.copy(plataforma = valor)
    }

    fun atualizarValorGanho(valor: String) {
        _uiState.value = _uiState.value.copy(valorGanho = valor)
    }

    fun atualizarKmRodado(valor: String) {
        _uiState.value = _uiState.value.copy(kmRodado = valor)
    }

    fun atualizarDuracao(valor: String) {
        _uiState.value = _uiState.value.copy(duracaoMinutos = valor)
    }

    fun atualizarObservacao(valor: String) {
        _uiState.value = _uiState.value.copy(observacao = valor)
    }

    fun salvar() {
        val estado = _uiState.value
        if (!estado.formularioValido) return

        viewModelScope.launch {
            repository.salvarCorrida(
                Corrida(
                    tipo = estado.tipo,
                    plataforma = estado.plataforma,
                    valorGanho = estado.valorGanho.replace(",", ".").toDoubleOrNull() ?: 0.0,
                    kmRodado = estado.kmRodado.replace(",", ".").toDoubleOrNull() ?: 0.0,
                    duracaoMinutos = estado.duracaoMinutos.toIntOrNull() ?: 0,
                    dataHora = System.currentTimeMillis(),
                    observacao = estado.observacao
                )
            )
            _uiState.value = NovaCorridaUiState(config = estado.config, salvoComSucesso = true)
        }
    }

    fun confirmarSalvo() {
        _uiState.value = _uiState.value.copy(salvoComSucesso = false)
    }
}
