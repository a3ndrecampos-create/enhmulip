package com.andrecampos.lucronarota.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Uma diária de trabalho: começa quando o motorista registra o km inicial
 * e termina quando ele registra o km final + o ganho do dia.
 *
 * custoPorKmSnapshot / custoFixoSnapshot guardam os custos do veículo no
 * momento em que a diária foi iniciada, para que uma mudança futura no preço
 * do combustível (por exemplo) não altere o resultado de diárias passadas.
 */
@Entity(tableName = "diarias")
data class Diaria(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val veiculoId: Long,
    val veiculoNome: String,
    val custoPorKmSnapshot: Double,
    val custoFixoSnapshot: Double,
    val kmInicial: Double,
    val kmFinal: Double? = null,
    val ganho: Double? = null,
    val inicioEm: Long,
    val fimEm: Long? = null,
    val observacao: String = "",
    val finalizada: Boolean = false
) {
    val kmRodado: Double
        get() = if (kmFinal != null) (kmFinal - kmInicial).coerceAtLeast(0.0) else 0.0

    val duracaoMinutos: Long
        get() = if (fimEm != null) ((fimEm - inicioEm) / 60000L).coerceAtLeast(0L) else 0L
}
