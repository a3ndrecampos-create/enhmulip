package com.andrecampos.lucronarota.ui.diaria

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.andrecampos.lucronarota.data.Abastecimento
import com.andrecampos.lucronarota.data.Diaria
import com.andrecampos.lucronarota.data.Veiculo
import com.andrecampos.lucronarota.ui.ViewModelFactory
import com.andrecampos.lucronarota.ui.components.BotaoLerKmCamera
import com.andrecampos.lucronarota.ui.theme.Emerald
import com.andrecampos.lucronarota.ui.theme.LucroNegativo
import com.andrecampos.lucronarota.util.Calculadora
import com.andrecampos.lucronarota.util.formatarDataHora
import com.andrecampos.lucronarota.util.formatarDuracao
import com.andrecampos.lucronarota.util.formatarKm
import com.andrecampos.lucronarota.util.formatarMoeda

private fun paraDouble(texto: String): Double? = texto.replace(",", ".").toDoubleOrNull()

@Composable
fun DiariaScreen(factory: ViewModelFactory) {
    val viewModel: DiariaViewModel = viewModel(factory = factory)
    val estado by viewModel.uiState.collectAsState()

    // Guardado com rememberSaveable: sobrevive mesmo se o Android encerrar o
    // processo do app com ele minimizado, então o motorista não perde o que
    // já digitou ao voltar pro app.
    var veiculoSelecionadoId by rememberSaveable { mutableStateOf<Long?>(null) }
    var kmInicialTexto by rememberSaveable { mutableStateOf("") }
    var kmFinalTexto by rememberSaveable { mutableStateOf("") }
    var ganhoTexto by rememberSaveable { mutableStateOf("") }
    var observacaoTexto by rememberSaveable { mutableStateOf("") }
    var erro by rememberSaveable { mutableStateOf<String?>(null) }

    var mostrarDialogoAbastecimento by rememberSaveable { mutableStateOf(false) }
    var abastecimentoValorTexto by rememberSaveable { mutableStateOf("") }
    var abastecimentoLitrosTexto by rememberSaveable { mutableStateOf("") }
    var abastecimentoLocalTexto by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(estado.veiculos) {
        if (veiculoSelecionadoId == null) {
            veiculoSelecionadoId = estado.veiculos.firstOrNull()?.id
        }
    }

    // Quando a diária muda de "aberta" pra "nenhuma" (acabou de ser
    // finalizada) ou de "nenhuma" pra "aberta" (acabou de ser iniciada),
    // limpa os campos daquela etapa pra próxima vez.
    LaunchedEffect(estado.diariaAberta?.id) {
        if (estado.diariaAberta == null) {
            kmFinalTexto = ""
            ganhoTexto = ""
            observacaoTexto = ""
        } else {
            kmInicialTexto = ""
        }
    }

    val diaria = estado.diariaAberta
    val kmFinalValor = paraDouble(kmFinalTexto)
    val ganhoValor = paraDouble(ganhoTexto)

    val podeIniciar = veiculoSelecionadoId != null && (paraDouble(kmInicialTexto) ?: -1.0) >= 0.0
    val podeFinalizar = diaria != null && kmFinalValor != null &&
        kmFinalValor > diaria.kmInicial && (ganhoValor ?: -1.0) >= 0.0

    val kmRodadoPrevisto = if (diaria != null && kmFinalValor != null) {
        (kmFinalValor - diaria.kmInicial).coerceAtLeast(0.0)
    } else 0.0

    val custoCombustivelPrevisto = if (estado.totalAbastecido > 0) {
        estado.totalAbastecido
    } else {
        kmRodadoPrevisto * (diaria?.custoCombustivelPorKmSnapshot ?: 0.0)
    }
    val custoPrevisto = custoCombustivelPrevisto +
        kmRodadoPrevisto * (diaria?.custoOutrosPorKmSnapshot ?: 0.0) +
        (diaria?.custoFixoSnapshot ?: 0.0)
    val lucroPrevisto = (ganhoValor ?: 0.0) - custoPrevisto

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(text = "Diária de trabalho", style = MaterialTheme.typography.headlineMedium)

        if (estado.veiculos.isEmpty()) {
            Text(
                text = "Cadastre um veículo na aba \"Veículos\" antes de iniciar sua diária.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else if (diaria == null) {
            TelaIniciarDiaria(
                veiculos = estado.veiculos,
                veiculoSelecionadoId = veiculoSelecionadoId,
                onSelecionarVeiculo = { veiculoSelecionadoId = it },
                kmInicialTexto = kmInicialTexto,
                onKmInicialChange = { kmInicialTexto = it; erro = null },
                podeIniciar = podeIniciar,
                onIniciar = {
                    val id = veiculoSelecionadoId
                    val km = paraDouble(kmInicialTexto)
                    if (id != null && km != null) viewModel.iniciarDiaria(id, km)
                },
                onErro = { erro = it }
            )
        } else {
            TelaFinalizarDiaria(
                diaria = diaria,
                abastecimentos = estado.abastecimentos,
                totalAbastecido = estado.totalAbastecido,
                kmFinalTexto = kmFinalTexto,
                onKmFinalChange = { kmFinalTexto = it; erro = null },
                ganhoTexto = ganhoTexto,
                onGanhoChange = { ganhoTexto = it; erro = null },
                observacaoTexto = observacaoTexto,
                onObservacaoChange = { observacaoTexto = it },
                kmRodadoPrevisto = kmRodadoPrevisto,
                custoPrevisto = custoPrevisto,
                lucroPrevisto = lucroPrevisto,
                podeFinalizar = podeFinalizar,
                onFinalizar = {
                    val km = kmFinalValor
                    val ganho = ganhoValor
                    if (km != null && ganho != null) {
                        viewModel.finalizarDiaria(km, ganho, observacaoTexto)
                    }
                },
                onAbrirDialogoAbastecimento = { mostrarDialogoAbastecimento = true },
                onRemoverAbastecimento = viewModel::removerAbastecimento,
                onErro = { erro = it }
            )
        }

        erro?.let { mensagem ->
            Text(
                text = mensagem,
                style = MaterialTheme.typography.bodyMedium,
                color = LucroNegativo
            )
        }
    }

    estado.ultimaDiariaFinalizada?.let { diariaFinalizada ->
        DialogoAnaliseDiaria(diaria = diariaFinalizada, aoFechar = viewModel::fecharResultado)
    }

    if (mostrarDialogoAbastecimento) {
        DialogoRegistrarAbastecimento(
            valorTexto = abastecimentoValorTexto,
            onValorChange = { abastecimentoValorTexto = it },
            litrosTexto = abastecimentoLitrosTexto,
            onLitrosChange = { abastecimentoLitrosTexto = it },
            localTexto = abastecimentoLocalTexto,
            onLocalChange = { abastecimentoLocalTexto = it },
            onConfirmar = {
                val valor = paraDouble(abastecimentoValorTexto)
                if (valor != null && valor > 0) {
                    viewModel.registrarAbastecimento(
                        valor,
                        paraDouble(abastecimentoLitrosTexto),
                        abastecimentoLocalTexto.trim()
                    )
                    abastecimentoValorTexto = ""
                    abastecimentoLitrosTexto = ""
                    abastecimentoLocalTexto = ""
                    mostrarDialogoAbastecimento = false
                }
            },
            onCancelar = { mostrarDialogoAbastecimento = false }
        )
    }
}

@Composable
private fun TelaIniciarDiaria(
    veiculos: List<Veiculo>,
    veiculoSelecionadoId: Long?,
    onSelecionarVeiculo: (Long) -> Unit,
    kmInicialTexto: String,
    onKmInicialChange: (String) -> Unit,
    podeIniciar: Boolean,
    onIniciar: () -> Unit,
    onErro: (String) -> Unit
) {
    Text(
        text = "Registre o km inicial para começar a diária",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    if (veiculos.size > 1) {
        Column {
            Text(
                text = "Veículo",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                veiculos.forEach { veiculo ->
                    val selecionado = veiculoSelecionadoId == veiculo.id
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (selecionado) Emerald.copy(alpha = 0.18f)
                                else MaterialTheme.colorScheme.surface
                            )
                            .clickable { onSelecionarVeiculo(veiculo.id) }
                    ) {
                        Text(
                            text = veiculo.nome,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            color = if (selecionado) Emerald else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }

    OutlinedTextField(
        value = kmInicialTexto,
        onValueChange = onKmInicialChange,
        label = { Text("Km inicial (painel do carro)") },
        textStyle = MaterialTheme.typography.titleLarge,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth()
    )

    BotaoLerKmCamera(
        onKmLido = onKmInicialChange,
        onErro = onErro,
        modifier = Modifier.fillMaxWidth()
    )

    Button(
        onClick = onIniciar,
        enabled = podeIniciar,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text("Iniciar diária", style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun TelaFinalizarDiaria(
    diaria: Diaria,
    abastecimentos: List<Abastecimento>,
    totalAbastecido: Double,
    kmFinalTexto: String,
    onKmFinalChange: (String) -> Unit,
    ganhoTexto: String,
    onGanhoChange: (String) -> Unit,
    observacaoTexto: String,
    onObservacaoChange: (String) -> Unit,
    kmRodadoPrevisto: Double,
    custoPrevisto: Double,
    lucroPrevisto: Double,
    podeFinalizar: Boolean,
    onFinalizar: () -> Unit,
    onAbrirDialogoAbastecimento: () -> Unit,
    onRemoverAbastecimento: (Abastecimento) -> Unit,
    onErro: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        Text("Diária em andamento", style = MaterialTheme.typography.titleLarge)
        Text(
            text = "${diaria.veiculoNome} · início às ${diaria.inicioEm.formatarDataHora()}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Km inicial: ${diaria.kmInicial.formatarKm()}",
            style = MaterialTheme.typography.titleMedium,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Abastecimentos de hoje", style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = onAbrirDialogoAbastecimento) {
                Icon(Icons.Filled.LocalGasStation, contentDescription = "Registrar abastecimento")
            }
        }

        if (abastecimentos.isEmpty()) {
            Text(
                text = "Nenhum abastecimento registrado. Se você não abastecer, o app usa a média configurada do veículo.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            abastecimentos.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = item.valor.formatarMoeda(),
                            style = MaterialTheme.typography.bodyLarge,
                            fontFamily = FontFamily.Monospace
                        )
                        val detalhe = listOfNotNull(
                            item.local.takeIf { it.isNotBlank() },
                            item.litros?.let { "$it L" }
                        ).joinToString("  ·  ")
                        if (detalhe.isNotBlank()) {
                            Text(
                                text = detalhe,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = { onRemoverAbastecimento(item) }) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Remover abastecimento",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Text(
                text = "Total abastecido: ${totalAbastecido.formatarMoeda()}",
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 6.dp)
            )
        }

        OutlinedButton(
            onClick = onAbrirDialogoAbastecimento,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            Icon(Icons.Filled.LocalGasStation, contentDescription = null)
            Text(" Registrar abastecimento", modifier = Modifier.padding(start = 6.dp))
        }
    }

    Text(
        text = "Registre o km final e o ganho do dia para encerrar",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    OutlinedTextField(
        value = kmFinalTexto,
        onValueChange = onKmFinalChange,
        label = { Text("Km final (painel do carro)") },
        textStyle = MaterialTheme.typography.titleLarge,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth()
    )

    BotaoLerKmCamera(
        onKmLido = onKmFinalChange,
        onErro = onErro,
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = ganhoTexto,
        onValueChange = onGanhoChange,
        label = { Text("Ganho total do dia (R$)") },
        textStyle = MaterialTheme.typography.titleLarge,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = observacaoTexto,
        onValueChange = onObservacaoChange,
        label = { Text("Observação — opcional") },
        modifier = Modifier.fillMaxWidth()
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        Text("Prévia do cálculo", style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Km rodado", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(kmRodadoPrevisto.formatarKm(), fontFamily = FontFamily.Monospace)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Custo estimado", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(custoPrevisto.formatarMoeda(), fontFamily = FontFamily.Monospace, color = LucroNegativo)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Lucro líquido estimado", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                lucroPrevisto.formatarMoeda(),
                fontFamily = FontFamily.Monospace,
                color = if (lucroPrevisto >= 0) Emerald else LucroNegativo
            )
        }
    }

    Button(
        onClick = onFinalizar,
        enabled = podeFinalizar,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text("Finalizar diária e ver análise", style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun DialogoRegistrarAbastecimento(
    valorTexto: String,
    onValorChange: (String) -> Unit,
    litrosTexto: String,
    onLitrosChange: (String) -> Unit,
    localTexto: String,
    onLocalChange: (String) -> Unit,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Registrar abastecimento") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = valorTexto,
                    onValueChange = onValorChange,
                    label = { Text("Valor abastecido (R$)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = litrosTexto,
                    onValueChange = onLitrosChange,
                    label = { Text("Litros — opcional") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = localTexto,
                    onValueChange = onLocalChange,
                    label = { Text("Posto — opcional") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirmar) { Text("Registrar") }
        },
        dismissButton = {
            OutlinedButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}

@Composable
private fun DialogoAnaliseDiaria(diaria: Diaria, aoFechar: () -> Unit) {
    val lucro = Calculadora.lucroLiquido(diaria)
    val custo = Calculadora.custoDaDiaria(diaria)
    val custoCombustivel = Calculadora.custoCombustivelDaDiaria(diaria)

    AlertDialog(
        onDismissRequest = aoFechar,
        confirmButton = {
            Button(onClick = aoFechar) { Text("Fechar") }
        },
        title = { Text("Análise da diária") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LinhaAnalise("Veículo", diaria.veiculoNome)
                LinhaAnalise("Km rodado", diaria.kmRodado.formatarKm())
                LinhaAnalise("Tempo trabalhado", formatarDuracao(diaria.duracaoMinutos))
                LinhaAnalise("Ganho bruto", (diaria.ganho ?: 0.0).formatarMoeda())
                LinhaAnalise("Combustível", custoCombustivel.formatarMoeda())
                LinhaAnalise("Custo total", custo.formatarMoeda())
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
        Text(rotulo, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = valor,
            style = if (destaque) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge,
            fontFamily = FontFamily.Monospace,
            color = if (destaque) (if (positivo) Emerald else LucroNegativo) else MaterialTheme.colorScheme.onSurface
        )
    }
}
