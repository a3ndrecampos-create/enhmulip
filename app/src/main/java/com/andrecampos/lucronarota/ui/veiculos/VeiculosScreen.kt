package com.andrecampos.lucronarota.ui.veiculos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.andrecampos.lucronarota.ui.ViewModelFactory
import com.andrecampos.lucronarota.ui.theme.Emerald
import kotlinx.coroutines.delay

@Composable
fun VeiculosScreen(factory: ViewModelFactory) {
    val viewModel: VeiculosViewModel = viewModel(factory = factory)
    val estado by viewModel.uiState.collectAsState()
    var mostrarConfirmacao by remember { mutableStateOf(false) }

    LaunchedEffect(estado.salvoComSucesso) {
        if (estado.salvoComSucesso) {
            mostrarConfirmacao = true
            delay(2000)
            mostrarConfirmacao = false
            viewModel.confirmarSalvo()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Veículos", style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "Cadastre cada carro que você usa para trabalhar. Cada um tem seus próprios custos.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (estado.veiculos.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                estado.veiculos.forEach { veiculo ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.DirectionsCar, contentDescription = null, tint = Emerald)
                                Column(modifier = Modifier.padding(start = 10.dp)) {
                                    Text(veiculo.nome, style = MaterialTheme.typography.titleMedium)
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Text(
                                            "R$ ${veiculo.precoCombustivel}/L",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontFamily = FontFamily.Monospace,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            "${veiculo.consumoKmPorLitro} km/L",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontFamily = FontFamily.Monospace,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                            IconButton(onClick = { viewModel.remover(veiculo) }) {
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = "Remover veículo",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        Text("Adicionar veículo", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = estado.nome,
            onValueChange = viewModel::atualizarNome,
            label = { Text("Nome do veículo (ex: Onix branco)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = estado.precoCombustivel,
            onValueChange = viewModel::atualizarPreco,
            label = { Text("Preço do combustível (R\$/litro)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = estado.consumoKmPorLitro,
            onValueChange = viewModel::atualizarConsumo,
            label = { Text("Consumo médio (km por litro)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = estado.outrosCustosPorKm,
            onValueChange = viewModel::atualizarOutrosCustos,
            label = { Text("Outros custos por km (manutenção, pneu, óleo)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = estado.custoFixoDiario,
            onValueChange = viewModel::atualizarCustoFixo,
            label = { Text("Custo fixo por diária (seguro, financiamento) — opcional") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = viewModel::salvar,
            enabled = estado.podeSalvar,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Salvar veículo")
        }

        if (mostrarConfirmacao) {
            Snackbar { Text("Veículo salvo com sucesso!") }
        }
    }
}
