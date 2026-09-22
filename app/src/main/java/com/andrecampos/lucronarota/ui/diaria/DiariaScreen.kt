package com.andrecampos.lucronarota.ui.diaria

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.andrecampos.lucronarota.data.Diaria
import com.andrecampos.lucronarota.ui.ViewModelFactory
import com.andrecampos.lucronarota.ui.components.BotaoLerKmCamera
import com.andrecampos.lucronarota.ui.theme.Emerald
import com.andrecampos.lucronarota.ui.theme.LucroNegativo
import com.andrecampos.lucronarota.util.Calculadora
import com.andrecampos.lucronarota.util.formatarDataHora
import com.andrecampos.lucronarota.util.formatarDuracao
import com.andrecampos.lucronarota.util.formatarKm
import com.andrecampos.lucronarota.util.formatarMoeda

@Composable
fun DiariaScreen(factory: ViewModelFactory) {
    val viewModel: DiariaViewModel = viewModel(factory = factory)
    val estado by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Diária de trabalho", style = MaterialTheme.typography.headlineMedium)

        if (estado.veiculos.isEmpty()) {
            Text(
                text = "Cadastre um veículo na aba \"Veículos\" antes de iniciar sua diária.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else if (estado.diariaAberta == null) {
            TelaIniciarDiaria(estado, viewModel)
        } else {
            TelaFinalizarDiaria(estado, viewModel)
        }

        estado.erro?.let { mensagem ->
            Text(
                text = mensagem,
                style = MaterialTheme.typography.bodyMedium,
                color = LucroNegativo
            )
        }
    }

    estado.ultimaDiariaFinalizada?.let { diaria ->
        DialogoAnaliseDiaria(diaria = diaria, aoFechar = viewModel::fecharResultado)
    }
}

@Composable
private fun TelaIniciarDiaria(estado: DiariaUiState, viewModel: DiariaViewModel) {
    Text(
        text = "Registre o km inicial para começar a diária",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    if (estado.veiculos.size > 1) {
        Column {
            Text(
                text = "Veículo",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                estado.veiculos.forEach { veiculo ->
                    val selecionado = estado.veiculoSelecionadoId == veiculo.id
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (selecionado) Emerald.copy(alpha = 0.18f)
                            else MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.clickable { viewModel.selecionarVeiculo(veiculo.id) }
                    ) {
                        Text(
                            text = veiculo.nome,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            color = if (selecionado) Emerald else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }

    OutlinedTextField(
        value = estado.kmInicialTexto,
        onValueChange = viewModel::atualizarKmInicial,
        label = { Text("Km inicial (painel do carro)") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth()
    )

    BotaoLerKmCamera(
        onKmLido = viewModel::atualizarKmInicial,
        onErro = viewModel::mostrarErro,
        modifier = Modifier.fillMaxWidth()
    )

    Button(
        onClick = viewModel::iniciarDiaria,
        enabled = estado.podeIniciar,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Text("Iniciar diária")
    }
}

@Composable
private fun TelaFinalizarDiaria(estado: DiariaUiState, viewModel: DiariaViewModel) {
    val diaria = estado.diariaAberta ?: return

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Diária em andamento", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "${diaria.veiculoNome} · início às ${diaria.inicioEm.formatarDataHora()}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Km inicial: ${diaria.kmInicial.formatarKm()}",
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    Text(
        text = "Registre o km final e o ganho do dia para encerrar",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    OutlinedTextField(
        value = estado.kmFinalTexto,
        onValueChange = viewModel::atualizarKmFinal,
        label = { Text("Km final (painel do carro)") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth()
    )

    BotaoLerKmCamera(
        onKmLido = viewModel::atualizarKmFinal,
        onErro = viewModel::mostrarErro,
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = estado.ganhoTexto,
        onValueChange = viewModel::atualizarGanho,
        label = { Text("Ganho total do dia (R$)") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = estado.observacaoTexto,
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
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Km rodado", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(estado.kmRodadoPrevisto.formatarKm(), fontFamily = FontFamily.Monospace)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Custo estimado", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(estado.custoPrevisto.formatarMoeda(), fontFamily = FontFamily.Monospace, color = LucroNegativo)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Lucro líquido estimado", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    estado.lucroPrevisto.formatarMoeda(),
                    fontFamily = FontFamily.Monospace,
                    color = if (estado.lucroPrevisto >= 0) Emerald else LucroNegativo
                )
            }
        }
    }

    Button(
        onClick = viewModel::finalizarDiaria,
        enabled = estado.podeFinalizar,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Text("Finalizar diária e ver análise")
    }
}

@Composable
private fun DialogoAnaliseDiaria(diaria: Diaria, aoFechar: () -> Unit) {
    val lucro = Calculadora.lucroLiquido(diaria)
    val custo = Calculadora.custoDaDiaria(diaria)

    AlertDialog(
        onDismissRequest = aoFechar,
        confirmButton = {
            Button(onClick = aoFechar) { Text("Fechar") }
        },
        title = { Text("Análise da diária") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                LinhaAnalise("Veículo", diaria.veiculoNome)
                LinhaAnalise("Km rodado", diaria.kmRodado.formatarKm())
                LinhaAnalise("Tempo trabalhado", formatarDuracao(diaria.duracaoMinutos))
                LinhaAnalise("Ganho bruto", (diaria.ganho ?: 0.0).formatarMoeda())
                LinhaAnalise("Custo estimado", custo.formatarMoeda())
                LinhaAnalise(
                    "Lucro líquido",
                    lucro.formatarMoeda(),
                    destaque = true,
                    positivo = lucro >= 0
                )
                LinhaAnalise("Ganho por km", Calculadora.ganhoPorKm(diaria).formatarMoeda())
                LinhaAnalise("Lucro por hora", Calculadora.lucroPorHora(diaria).formatarMoeda())
            }
        }
    )
}

@Composable
private fun LinhaAnalise(
    rotulo: String,
    valor: String,
    destaque: Boolean = false,
    positivo: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(rotulo, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = valor,
            style = if (destaque) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontFamily = FontFamily.Monospace,
            color = if (destaque) (if (positivo) Emerald else LucroNegativo) else MaterialTheme.colorScheme.onSurface
        )
    }
}
