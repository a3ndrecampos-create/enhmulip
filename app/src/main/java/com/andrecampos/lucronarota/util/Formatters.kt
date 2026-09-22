package com.andrecampos.lucronarota.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

private val localeBR = Locale("pt", "BR")
private val moedaFormat = NumberFormat.getCurrencyInstance(localeBR)
private val dataHoraFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", localeBR)
private val dataFormat = SimpleDateFormat("dd/MM", localeBR)

fun Double.formatarMoeda(): String = moedaFormat.format(this)

fun Double.formatarKm(): String = String.format(localeBR, "%.1f km", this)

fun Double.formatarDecimal(casas: Int = 2): String = String.format(localeBR, "%.${casas}f", this)

fun Long.formatarDataHora(): String = dataHoraFormat.format(this)

fun Long.formatarData(): String = dataFormat.format(this)

/** Recebe uma duração em minutos e formata como "Xh Ymin" (ou só "Ymin"). */
fun formatarDuracao(minutos: Long): String {
    val horas = minutos / 60
    val resto = minutos % 60
    return if (horas > 0) "${horas}h ${resto}min" else "${resto}min"
}
