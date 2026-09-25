package com.andrecampos.lucronarota.ui.diaria

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrecampos.lucronarota.data.Abastecimento
import com.andrecampos.lucronarota.data.Diaria
import com.andrecampos.lucronarota.data.DiariaRepository
import com.andrecampos.lucronarota.data.Veiculo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Este estado só guarda o que vem do banco (veículos, diária aberta,
 * abastecimentos). Os campos de texto que o usuário está digitando (km,
 * ganho, etc.) ficam na tela com rememberSaveable, não aqui — porque um
 * ViewModel some se o Android matar o processo do app em segundo plano,
 * mas o rememberSaveable sobrevive. Isso evita perder o que foi digitado
 * se o motorista minimizar o app no meio do registro.
 */
data class DiariaUiState(
    val veiculos: List<Veiculo> = emptyList(),
    val diariaAberta: Diaria? = null,
    val abastecimentos: List<Abastecimento> = emptyList(),
    val ultimaDiariaFinalizada: Diaria? = null
) {
    val totalAbastecido: Double get() = abastecimentos.sumOf { it.valor }
}

class DiariaViewModel(private val repository: DiariaRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(DiariaUiState())
    val uiState: StateFlow<DiariaUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.observarVeiculos(),
                repository.observarDiariaAberta()
            ) { veiculos, aberta -> veiculos to aberta }
                .flatMapLatest { (veiculos, aberta) ->
                    val abastecimentosFlow = if (aberta != null) {
                        repository.observarAbastecimentos(aberta.id)
                    } else {
                        flowOf(emptyList())
                    }
                    abastecimentosFlow.map { abastecimentos ->
                        DiariaUiState(
                            veiculos = veiculos,
                            diariaAberta = aberta,
                            abastecimentos = abastecimentos
                        )
                    }
                }
                .collect { novo ->
                    _uiState.value = novo.copy(
                        ultimaDiariaFinalizada = _uiState.value.ultimaDiariaFinalizada
                    )
                }
        }
    }

    fun iniciarDiaria(veiculoId: Long, kmInicial: Double) {
        val veiculo = _uiState.value.veiculos.find { it.id == veiculoId } ?: return
        viewModelScope.launch { repository.iniciarDiaria(veiculo, kmInicial) }
    }

    fun registrarAbastecimento(valor: Double, litros: Double?, local: String) {
        val diaria = _uiState.value.diariaAberta ?: return
        viewModelScope.launch { repository.registrarAbastecimento(diaria.id, valor, litros, local) }
    }

    fun removerAbastecimento(abastecimento: Abastecimento) {
        viewModelScope.launch { repository.removerAbastecimento(abastecimento) }
    }

    fun finalizarDiaria(kmFinal: Double, ganho: Double, observacao: String) {
        val diaria = _uiState.value.diariaAberta ?: return
        viewModelScope.launch {
            val finalizada = repository.finalizarDiaria(diaria, kmFinal, ganho, observacao)
            _uiState.value = _uiState.value.copy(ultimaDiariaFinalizada = finalizada)
        }
    }

    fun fecharResultado() {
        _uiState.value = _uiState.value.copy(ultimaDiariaFinalizada = null)
    }
}
