package com.andrecampos.lucronarota.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrecampos.lucronarota.data.ConfigVeiculo
import com.andrecampos.lucronarota.data.Corrida
import com.andrecampos.lucronarota.data.CorridaRepository
import com.andrecampos.lucronarota.util.Calculadora
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Calendar

enum class Periodo(val rotulo: String) {
    HOJE("Hoje"),
    SEMANA("7 dias"),
    MES("30 dias"),
    TUDO("Tudo")
}

data class DashboardUiState(
    val periodo: Periodo = Periodo.HOJE,
    val resumo: Calculadora.Resumo = Calculadora.Resumo(),
    val corridasRecentes: List<Corrida> = emptyList(),
    val config: ConfigVeiculo = ConfigVeiculo()
)

class DashboardViewModel(private val repository: CorridaRepository) : ViewModel() {

    private val periodoSelecionado = MutableStateFlow(Periodo.HOJE)

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                periodoSelecionado,
                repository.observarConfig()
            ) { periodo, config -> Pair(periodo, config ?: ConfigVeiculo()) }
                .flatMapLatest { (periodo, config) ->
                    val inicio = calcularInicio(periodo)
                    val corridasFlow = if (inicio == null) repository.observarCorridas()
                    else repository.observarCorridasDesde(inicio)

                    corridasFlow.map { corridas ->
                        DashboardUiState(
                            periodo = periodo,
                            resumo = Calculadora.resumir(corridas, config),
                            corridasRecentes = corridas.take(5),
                            config = config
                        )
                    }
                }
                .collect { _uiState.value = it }
        }
    }

    private fun calcularInicio(periodo: Periodo): Long? {
        val cal = Calendar.getInstance()
        return when (periodo) {
            Periodo.HOJE -> {
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis
            }
            Periodo.SEMANA -> {
                cal.add(Calendar.DAY_OF_YEAR, -7)
                cal.timeInMillis
            }
            Periodo.MES -> {
                cal.add(Calendar.DAY_OF_YEAR, -30)
                cal.timeInMillis
            }
            Periodo.TUDO -> null
        }
    }

    fun selecionarPeriodo(periodo: Periodo) {
        periodoSelecionado.value = periodo
    }
}
