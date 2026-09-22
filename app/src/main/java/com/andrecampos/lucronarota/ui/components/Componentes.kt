package com.andrecampos.lucronarota.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.andrecampos.lucronarota.data.Diaria
import com.andrecampos.lucronarota.ui.theme.Emerald
import com.andrecampos.lucronarota.ui.theme.LucroNegativo
import com.andrecampos.lucronarota.util.Calculadora
import com.andrecampos.lucronarota.util.formatarData
import com.andrecampos.lucronarota.util.formatarKm
import com.andrecampos.lucronarota.util.formatarMoeda

/**
 * O painel principal do Dashboard: o número de lucro é lido como um
 * odômetro (fonte monoespaçada), com uma barra fina mostrando a proporção
 * entre o que foi gasto e o que foi ganho — em vez de mais um card genérico
 * igual aos outros.
 */
@Composable
fun CartaoLucroPrincipal(
    lucro: Double,
    ganho: Double,
    custo: Double,
    subtitulo: String,
    modifier: Modifier = Modifier
) {
    val positivo = lucro >= 0
    val corDestaque = if (positivo) Emerald else LucroNegativo
    val proporcaoCusto = if (ganho > 0) (custo / ganho).toFloat().coerceIn(0f, 1f) else 0f

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(20.dp)
    ) {
        Text(
            text = subtitulo,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = lucro.formatarMoeda(),
            style = MaterialTheme.typography.displaySmall,
            color = corDestaque,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(top = 6.dp, bottom = 16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(proporcaoCusto)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(LucroNegativo)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "ganho ${ganho.formatarMoeda()}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "custo ${custo.formatarMoeda()}",
                style = MaterialTheme.typography.labelMedium,
                color = LucroNegativo
            )
        }
    }
}

/** Uma linha simples "rótulo / valor" para agrupar estatísticas num único
 * painel, com divisores finos entre elas — em vez de repetir um card por
 * estatística. */
@Composable
fun LinhaInfo(
    rotulo: String,
    valor: String,
    modifier: Modifier = Modifier,
    corValor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = rotulo,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.titleMedium,
            fontFamily = FontFamily.Monospace,
            color = corValor
        )
    }
}

@Composable
fun DivisorSutil(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier,
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant
    )
}

/** Item de lista de uma diária: uma barra colorida na lateral marca lucro
 * (verde) ou prejuízo (vermelho) daquele dia — em vez de um ícone circular
 * repetido em toda linha. */
@Composable
fun DiariaListItem(
    diaria: Diaria,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {
    val lucro = Calculadora.lucroLiquido(diaria)
    val corDestaque = if (lucro >= 0) Emerald else LucroNegativo

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(corDestaque)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Text(text = diaria.veiculoNome, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "${(diaria.fimEm ?: diaria.inicioEm).formatarData()} · ${diaria.kmRodado.formatarKm()} rodados",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(end = 12.dp)
        ) {
            Text(
                text = (diaria.ganho ?: 0.0).formatarMoeda(),
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = lucro.formatarMoeda(),
                style = MaterialTheme.typography.labelMedium,
                fontFamily = FontFamily.Monospace,
                color = corDestaque
            )
        }
        if (onDelete != null) {
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Remover diária",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SecaoTitulo(texto: String, modifier: Modifier = Modifier) {
    Text(
        text = texto,
        style = MaterialTheme.typography.titleLarge,
        modifier = modifier.padding(top = 4.dp, bottom = 2.dp)
    )
}

val cardPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
