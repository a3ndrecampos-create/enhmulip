package com.andrecampos.lucronarota.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "veiculos")
data class Veiculo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    val precoCombustivel: Double = 6.10,
    val consumoKmPorLitro: Double = 12.0,
    val outrosCustosPorKm: Double = 0.15,
    val custoFixoDiario: Double = 20.0,
    val criadoEm: Long = System.currentTimeMillis()
)
