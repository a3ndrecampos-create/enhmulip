package com.andrecampos.lucronarota.util

import com.andrecampos.lucronarota.data.ConfigVeiculo
import com.andrecampos.lucronarota.data.Corrida

/**
 * Regras de cálculo do app:
 * - custoPorKm = (preço do combustível / consumo km por litro) + outros custos por km
 *   (outros custos por km cobre desgaste de pneu, óleo, manutenção estimada).
 * - custoDaCorrida = kmRodado * custoPorKm
 * - lucroLiquido = valorGanho - custoDaCorrida
 * - ganhoPorKm / ganhoPorHora ajudam a comparar corridas entre si.
 */
object Calculadora {

    fun custoPorKm(config: ConfigVeiculo): Double {
        val custoCombustivelPorKm = if (config.consumoKmPorLitro > 0) {
            config.precoCombustivel / config.consumoKmPorLitro
        } else 0.0
        return custoCombustivelPorKm + config.outrosCustosPorKm
    }

    fun custoDaCorrida(corrida: Corrida, config: ConfigVeiculo): Double =
        corrida.kmRodado * custoPorKm(config)

    fun lucroLiquido(corrida: Corrida, config: ConfigVeiculo): Double =
        corrida.valorGanho - custoDaCorrida(corrida, config)

    fun ganhoPorKm(corrida: Corrida): Double =
        if (corrida.kmRodado > 0) corrida.valorGanho / corrida.kmRodado else 0.0

    fun ganhoPorHora(corrida: Corrida): Double =
        if (corrida.duracaoMinutos > 0) corrida.valorGanho / (corrida.duracaoMinutos / 60.0) else 0.0

    data class Resumo(
        val totalGanho: Double = 0.0,
        val totalKm: Double = 0.0,
        val totalCusto: Double = 0.0,
        val totalLucro: Double = 0.0,
        val totalCorridas: Int = 0,
        val totalMinutos: Int = 0
    ) {
        val lucroPorKm: Double get() = if (totalKm > 0) totalLucro / totalKm else 0.0
        val lucroPorHora: Double get() = if (totalMinutos > 0) totalLucro / (totalMinutos / 60.0) else 0.0
        val ticketMedio: Double get() = if (totalCorridas > 0) totalGanho / totalCorridas else 0.0
    }

    fun resumir(corridas: List<Corrida>, config: ConfigVeiculo): Resumo {
        if (corridas.isEmpty()) return Resumo()
        var ganho = 0.0
        var km = 0.0
        var custo = 0.0
        var minutos = 0
        for (c in corridas) {
            ganho += c.valorGanho
            km += c.kmRodado
            custo += custoDaCorrida(c, config)
            minutos += c.duracaoMinutos
        }
        return Resumo(
            totalGanho = ganho,
            totalKm = km,
            totalCusto = custo,
            totalLucro = ganho - custo,
            totalCorridas = corridas.size,
            totalMinutos = minutos
        )
    }
}
