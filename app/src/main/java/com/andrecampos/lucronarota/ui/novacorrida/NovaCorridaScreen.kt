package com.andrecampos.lucronarota.ui.novacorrida

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import com.andrecampos.lucronarota.data.TipoCorrida
import com.andrecampos.lucronarota.ui.ViewModelFactory
import com.andrecampos.lucronarota.ui.theme.Emerald
import com.andrecampos.lucronarota.ui.theme.LucroNegativo
import com.andrecampos.lucronarota.util.formatarMoeda
import kotlinx.coroutines.delay

private val plataformasApp = listOf("Uber", "99", "InDrive", "Outro app")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovaCorridaScreen(factory: ViewModelFactory) {
    val viewModel: NovaCorridaViewModel = viewModel(factory = factory)
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
        Text(text = "Nova corrida", style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "Registre uma corrida por app ou particular",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = estado.tipo == TipoCorrida.APP,
                onClick = { viewModel.atualizarTipo(TipoCorrida.APP) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
            ) { Text("Corrida por app") }
            SegmentedButton(
                selected = estado.tipo == TipoCorrida.PARTICULAR,
                onClick = { viewModel.atualizarTipo(TipoCorrida.PARTICULAR) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
            ) { Text("Particular") }
        }

        if (estado.tipo == TipoCorrida.APP) {
            Column {
                Text(
                    text = "Plataforma",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    plataformasApp.forEach { plataforma ->
                        val selecionada = estado.plataforma == plataforma
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (selecionada) Emerald.copy(alpha = 0.18f)
                                else MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.clickable { viewModel.atualizarPlataforma(plataforma) }
                        ) {
                            Text(
                                text = plataforma,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                color = if (selecionada) Emerald else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        } else {
            OutlinedTextField(
                value = estado.plataforma,
                onValueChange = viewModel::atualizarPlataforma,
                label = { Text("Descrição (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        OutlinedTextField(
            value = estado.valorGanho,
            onValueChange = viewModel::atualizarValorGanho,
            label = { Text("Valor recebido (R$)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = estado.kmRodado,
            onValueChange = viewModel::atualizarKmRodado,
            label = { Text("Km rodado nessa corrida") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = estado.duracaoMinutos,
            onValueChange = viewModel::atualizarDuracao,
            label = { Text("Duração (minutos) — opcional") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = estado.observacao,
            onValueChange = viewModel::atualizarObservacao,
            label = { Text("Observação — opcional") },
            modifier = Modifier.fillMaxWidth()
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Prévia do cálculo", style = MaterialTheme.typography.titleMedium)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Custo estimado", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(estado.custoEstimado.formatarMoeda(), color = LucroNegativo)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Lucro líquido estimado", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        estado.lucroEstimado.formatarMoeda(),
                        color = if (estado.lucroEstimado >= 0) Emerald else LucroNegativo
                    )
                }
            }
        }

        Button(
            onClick = { viewModel.salvar() },
            enabled = estado.formularioValido,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Salvar corrida")
        }

        if (mostrarConfirmacao) {
            Snackbar { Text("Corrida salva com sucesso!") }
        }
    }
}
