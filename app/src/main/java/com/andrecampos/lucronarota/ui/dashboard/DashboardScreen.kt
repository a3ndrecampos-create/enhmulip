package com.andrecampos.lucronarota.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.andrecampos.lucronarota.ui.ViewModelFactory
import com.andrecampos.lucronarota.ui.components.CartaoLucroPrincipal
import com.andrecampos.lucronarota.ui.components.DiariaListItem
import com.andrecampos.lucronarota.ui.components.DivisorSutil
import com.andrecampos.lucronarota.ui.components.LinhaInfo
import com.andrecampos.lucronarota.ui.components.SecaoTitulo
import com.andrecampos.lucronarota.util.formatarKm
import com.andrecampos.lucronarota.util.formatarMoeda

@Composable
fun DashboardScreen(factory: ViewModelFactory) {
    val viewModel: DashboardViewModel = viewModel(factory = factory)
    val estado by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(text = "Lucro na Rota", style = MaterialTheme.typography.headlineMedium)
            Text(
                text = "Seu painel de ganhos e custos",
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
            CartaoLucroPrincipal(
                lucro = estado.resumo.totalLucro,
                ganho = estado.resumo.totalGanho,
                custo = estado.resumo.totalCusto,
                subtitulo = "${estado.resumo.totalDiarias} diária(s) · ${estado.resumo.totalKm.formatarKm()} rodados",
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp)
            ) {
                LinhaInfo("Lucro por km", estado.resumo.lucroPorKm.formatarMoeda())
                DivisorSutil()
                LinhaInfo("Lucro por hora", estado.resumo.lucroPorHora.formatarMoeda())
                DivisorSutil()
                LinhaInfo("Ticket médio por diária", estado.resumo.ticketMedio.formatarMoeda())
            }
        }

        item { SecaoTitulo("Diárias recentes") }

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

