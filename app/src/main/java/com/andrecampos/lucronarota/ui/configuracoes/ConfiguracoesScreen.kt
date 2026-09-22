package com.andrecampos.lucronarota.ui.configuracoes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.andrecampos.lucronarota.ui.ViewModelFactory
import kotlinx.coroutines.delay

@Composable
fun ConfiguracoesScreen(factory: ViewModelFactory) {
    val viewModel: ConfiguracoesViewModel = viewModel(factory = factory)
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
        Text(text = "Ajustes do veículo", style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "Esses dados definem como o app calcula o custo de cada km rodado",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
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
            label = { Text("Custo fixo diário (seguro, financiamento) — opcional") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { viewModel.salvar() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Salvar ajustes")
        }

        if (mostrarConfirmacao) {
            Snackbar { Text("Ajustes salvos com sucesso!") }
        }
    }
}
