package com.andrecampos.lucronarota.ui.diaria

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

data class DiariaUiState(
    val veiculos: List<Veiculo> = emptyList(),
    val veiculoSelecionadoId: Long? = null,
    val diariaAberta: Diaria? = null,
    val kmInicialTexto: String = "",
    val kmFinalTexto: String = "",
    val ganhoTexto: String = "",
    val observacaoTexto: String = "",
    val ultimaDiariaFinalizada: Diaria? = null,
    val erro: String? = null
) {
    private fun paraDouble(texto: String) = texto.replace(",", ".").toDoubleOrNull()

    val podeIniciar: Boolean
        get() = veiculoSelecionadoId != null && (paraDouble(kmInicialTexto) ?: -1.0) >= 0.0

    val podeFinalizar: Boolean
        get() {
            val diaria = diariaAberta ?: return false
            val kmFinal = paraDouble(kmFinalTexto) ?: return false
            val ganho = paraDouble(ganhoTexto) ?: return false
            return kmFinal > diaria.kmInicial && ganho >= 0.0
        }

    val kmRodadoPrevisto: Double
        get() {
            val diaria = diariaAberta ?: return 0.0
            val kmFinal = paraDouble(kmFinalTexto) ?: return 0.0
            return (kmFinal - diaria.kmInicial).coerceAtLeast(0.0)
        }

    val custoPrevisto: Double
        get() {
            val diaria = diariaAberta ?: return 0.0
            return kmRodadoPrevisto * diaria.custoPorKmSnapshot + diaria.custoFixoSnapshot
        }

    val lucroPrevisto: Double
        get() = (paraDouble(ganhoTexto) ?: 0.0) - custoPrevisto
}

class DiariaViewModel(private val repository: DiariaRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(DiariaUiState())
    val uiState: StateFlow<DiariaUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.observarVeiculos(),
                repository.observarDiariaAberta()
            ) { veiculos, aberta -> Pair(veiculos, aberta) }
                .collect { (veiculos, aberta) ->
                    val atual = _uiState.value
                    _uiState.value = atual.copy(
                        veiculos = veiculos,
                        diariaAberta = aberta,
                        veiculoSelecionadoId = atual.veiculoSelecionadoId
                            ?: veiculos.firstOrNull()?.id
                    )
                }
        }
    }

    fun selecionarVeiculo(id: Long) {
        _uiState.value = _uiState.value.copy(veiculoSelecionadoId = id)
    }

    fun atualizarKmInicial(valor: String) {
        _uiState.value = _uiState.value.copy(kmInicialTexto = valor, erro = null)
    }

    fun atualizarKmFinal(valor: String) {
        _uiState.value = _uiState.value.copy(kmFinalTexto = valor, erro = null)
    }

    fun atualizarGanho(valor: String) {
        _uiState.value = _uiState.value.copy(ganhoTexto = valor, erro = null)
    }

    fun atualizarObservacao(valor: String) {
        _uiState.value = _uiState.value.copy(observacaoTexto = valor)
    }

    fun mostrarErro(mensagem: String) {
        _uiState.value = _uiState.value.copy(erro = mensagem)
    }

    fun limparErro() {
        _uiState.value = _uiState.value.copy(erro = null)
    }

    fun iniciarDiaria() {
        val estado = _uiState.value
        val veiculo = estado.veiculos.find { it.id == estado.veiculoSelecionadoId } ?: return
        val km = estado.kmInicialTexto.replace(",", ".").toDoubleOrNull() ?: return

        viewModelScope.launch {
            repository.iniciarDiaria(veiculo, km)
            _uiState.value = _uiState.value.copy(kmInicialTexto = "")
        }
    }

    fun finalizarDiaria() {
        val estado = _uiState.value
        val diaria = estado.diariaAberta ?: return
        val kmFinal = estado.kmFinalTexto.replace(",", ".").toDoubleOrNull() ?: return
        val ganho = estado.ganhoTexto.replace(",", ".").toDoubleOrNull() ?: return

        viewModelScope.launch {
            val finalizada = repository.finalizarDiaria(diaria, kmFinal, ganho, estado.observacaoTexto)
            _uiState.value = _uiState.value.copy(
                kmFinalTexto = "",
                ganhoTexto = "",
                observacaoTexto = "",
                ultimaDiariaFinalizada = finalizada
            )
        }
    }

    fun fecharResultado() {
        _uiState.value = _uiState.value.copy(ultimaDiariaFinalizada = null)
    }
}
