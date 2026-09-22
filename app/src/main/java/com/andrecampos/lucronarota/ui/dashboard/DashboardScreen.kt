package com.andrecampos.lucronarota.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.andrecampos.lucronarota.ui.ViewModelFactory
import com.andrecampos.lucronarota.ui.components.DiariaListItem
import com.andrecampos.lucronarota.ui.components.SecaoTitulo
import com.andrecampos.lucronarota.ui.components.StatCard
import com.andrecampos.lucronarota.ui.theme.Emerald
import com.andrecampos.lucronarota.ui.theme.LucroNegativo
import com.andrecampos.lucronarota.util.formatarKm
import com.andrecampos.lucronarota.util.formatarMoeda

@Composable
fun DashboardScreen(factory: ViewModelFactory) {
    val viewModel: DashboardViewModel = viewModel(factory = factory)
    val estado by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Lucro na Rota",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Seu resumo de ganhos e custos",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Periodo.entries.forEach { periodo ->
                    FilterChip(
                        selected = estado.periodo == periodo,
                        onClick = { viewModel.selecionarPeriodo(periodo) },
                        label = { Text(periodo.rotulo) }
                    )
                }
            }
        }

        item {
            val lucroPositivo = estado.resumo.totalLucro >= 0
            StatCard(
                titulo = "Lucro líquido",
                valor = estado.resumo.totalLucro.formatarMoeda(),
                corValor = if (lucroPositivo) Emerald else LucroNegativo,
                subtitulo = "${estado.resumo.totalDiarias} diária(s) · ${estado.resumo.totalKm.formatarKm()}",
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StatCard(
                    titulo = "Ganho bruto",
                    valor = estado.resumo.totalGanho.formatarMoeda(),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    titulo = "Custo estimado",
                    valor = estado.resumo.totalCusto.formatarMoeda(),
                    corValor = LucroNegativo,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StatCard(
                    titulo = "Lucro por km",
                    valor = estado.resumo.lucroPorKm.formatarMoeda(),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    titulo = "Lucro por hora",
                    valor = estado.resumo.lucroPorHora.formatarMoeda(),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            SecaoTitulo("Diárias recentes")
        }

        if (estado.diariasRecentes.isEmpty()) {
            item {
                Text(
                    text = "Nenhuma diária finalizada ainda. Vá em \"Diária\" para começar a sua.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(estado.diariasRecentes, key = { it.id }) { diaria ->
            DiariaListItem(diaria = diaria)
        }
    }
}
