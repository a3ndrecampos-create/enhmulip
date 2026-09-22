package com.andrecampos.lucronarota.util

import com.andrecampos.lucronarota.data.Diaria
import com.andrecampos.lucronarota.data.Veiculo

/**
 * Regras de cálculo do app:
 * - custoPorKm = (preço do combustível / consumo km por litro) + outros custos por km
 *   (outros custos por km cobre desgaste de pneu, óleo, manutenção estimada).
 * - custoDaDiaria = kmRodado * custoPorKm + custoFixoDiario
 * - lucroLiquido = ganho - custoDaDiaria
 */
object Calculadora {

    fun custoPorKm(veiculo: Veiculo): Double {
        val custoCombustivelPorKm = if (veiculo.consumoKmPorLitro > 0) {
            veiculo.precoCombustivel / veiculo.consumoKmPorLitro
        } else 0.0
        return custoCombustivelPorKm + veiculo.outrosCustosPorKm
    }

    fun custoDaDiaria(diaria: Diaria): Double =
        diaria.kmRodado * diaria.custoPorKmSnapshot + diaria.custoFixoSnapshot

    fun lucroLiquido(diaria: Diaria): Double = (diaria.ganho ?: 0.0) - custoDaDiaria(diaria)

    fun ganhoPorKm(diaria: Diaria): Double =
        if (diaria.kmRodado > 0) (diaria.ganho ?: 0.0) / diaria.kmRodado else 0.0

    fun lucroPorHora(diaria: Diaria): Double =
        if (diaria.duracaoMinutos > 0) lucroLiquido(diaria) / (diaria.duracaoMinutos / 60.0) else 0.0

    data class Resumo(
        val totalGanho: Double = 0.0,
        val totalKm: Double = 0.0,
        val totalCusto: Double = 0.0,
        val totalLucro: Double = 0.0,
        val totalDiarias: Int = 0,
        val totalMinutos: Long = 0
    ) {
        val lucroPorKm: Double get() = if (totalKm > 0) totalLucro / totalKm else 0.0
        val lucroPorHora: Double get() = if (totalMinutos > 0) totalLucro / (totalMinutos / 60.0) else 0.0
        val ticketMedio: Double get() = if (totalDiarias > 0) totalGanho / totalDiarias else 0.0
    }

    fun resumir(diarias: List<Diaria>): Resumo {
        if (diarias.isEmpty()) return Resumo()
        var ganho = 0.0
        var km = 0.0
        var custo = 0.0
        var minutos = 0L
        for (d in diarias) {
            ganho += d.ganho ?: 0.0
            km += d.kmRodado
            custo += custoDaDiaria(d)
            minutos += d.duracaoMinutos
        }
        return Resumo(
            totalGanho = ganho,
            totalKm = km,
            totalCusto = custo,
            totalLucro = ganho - custo,
            totalDiarias = diarias.size,
            totalMinutos = minutos
        )
    }
}
