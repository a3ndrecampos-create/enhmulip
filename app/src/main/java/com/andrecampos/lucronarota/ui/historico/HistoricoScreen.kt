package com.andrecampos.lucronarota.ui.historico

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

@Composable
fun HistoricoScreen(factory: ViewModelFactory) {
    val viewModel: HistoricoViewModel = viewModel(factory = factory)
    val estado by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(text = "Histórico", style = MaterialTheme.typography.headlineMedium)
        }

        if (estado.veiculos.size > 1) {
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        selected = estado.filtroVeiculoId == null,
                        onClick = { viewModel.selecionarFiltro(null) },
                        label = { Text("Todos") }
                    )
                    estado.veiculos.forEach { veiculo ->
                        FilterChip(
                            selected = estado.filtroVeiculoId == veiculo.id,
                            onClick = { viewModel.selecionarFiltro(veiculo.id) },
                            label = { Text(veiculo.nome) }
                        )
                    }
                }
            }
        }

        if (estado.diarias.isEmpty()) {
            item {
                Text(
                    text = "Nenhuma diária encontrada para esse filtro.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(estado.diarias, key = { it.id }) { diaria ->
            DiariaListItem(
                diaria = diaria,
                onDelete = { viewModel.remover(diaria) }
            )
        }
    }
}
