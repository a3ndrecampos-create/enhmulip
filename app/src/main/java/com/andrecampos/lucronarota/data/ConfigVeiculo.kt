package com.andrecampos.lucronarota.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "config_veiculo")
data class ConfigVeiculo(
    @PrimaryKey
    val id: Int = 1,
    val precoCombustivel: Double = 6.10,
    val consumoKmPorLitro: Double = 12.0,
    val outrosCustosPorKm: Double = 0.15,
    val custoFixoDiario: Double = 20.0
)
