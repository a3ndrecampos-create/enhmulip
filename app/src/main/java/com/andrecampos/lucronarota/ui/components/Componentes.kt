package com.andrecampos.lucronarota.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.andrecampos.lucronarota.data.Corrida
import com.andrecampos.lucronarota.data.TipoCorrida
import com.andrecampos.lucronarota.ui.theme.AzulParticular
import com.andrecampos.lucronarota.ui.theme.Emerald
import com.andrecampos.lucronarota.ui.theme.LucroNegativo
import com.andrecampos.lucronarota.util.formatarData
import com.andrecampos.lucronarota.util.formatarMoeda

@Composable
fun StatCard(
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier,
    corValor: Color = MaterialTheme.colorScheme.onSurface,
    subtitulo: String? = null
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = valor,
                style = MaterialTheme.typography.headlineSmall,
                color = corValor,
                modifier = Modifier.padding(top = 4.dp)
            )
            if (subtitulo != null) {
                Text(
                    text = subtitulo,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
fun CorridaListItem(
    corrida: Corrida,
    lucro: Double,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {
    val corTipo = if (corrida.tipo == TipoCorrida.APP) Emerald else AzulParticular
    val corLucro = if (lucro >= 0) Emerald else LucroNegativo

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(corTipo.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (corrida.tipo == TipoCorrida.APP) Icons.Filled.DirectionsCar else Icons.Filled.Person,
                        contentDescription = null,
                        tint = corTipo
                    )
                }
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        text = corrida.plataforma.ifBlank {
                            if (corrida.tipo == TipoCorrida.APP) "Corrida por app" else "Corrida particular"
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${corrida.dataHora.formatarData()} · ${corrida.kmRodado} km",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = corrida.valorGanho.formatarMoeda(),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "lucro ${lucro.formatarMoeda()}",
                    style = MaterialTheme.typography.labelMedium,
                    color = corLucro
                )
            }
            if (onDelete != null) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Remover corrida",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun SecaoTitulo(texto: String, modifier: Modifier = Modifier) {
    Text(
        text = texto,
        style = MaterialTheme.typography.titleLarge,
        modifier = modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

val cardPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
