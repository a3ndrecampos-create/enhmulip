package com.andrecampos.lucronarota.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrecampos.lucronarota.data.Diaria
import com.andrecampos.lucronarota.data.DiariaRepository
import com.andrecampos.lucronarota.util.Calculadora
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    val diariasRecentes: List<Diaria> = emptyList()
)

class DashboardViewModel(private val repository: DiariaRepository) : ViewModel() {

    private val periodoSelecionado = MutableStateFlow(Periodo.HOJE)

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            periodoSelecionado
                .flatMapLatest { periodo ->
                    val inicio = calcularInicio(periodo)
                    val diariasFlow = if (inicio == null) repository.observarFinalizadas()
                    else repository.observarFinalizadasDesde(inicio)

                    diariasFlow.map { diarias ->
                        DashboardUiState(
                            periodo = periodo,
                            resumo = Calculadora.resumir(diarias),
                            diariasRecentes = diarias.take(5)
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
