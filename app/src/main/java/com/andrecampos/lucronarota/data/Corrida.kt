package com.andrecampos.lucronarota.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TipoCorrida {
    APP,
    PARTICULAR
}

@Entity(tableName = "corridas")
data class Corrida(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tipo: TipoCorrida,
    val plataforma: String,
    val valorGanho: Double,
    val kmRodado: Double,
    val duracaoMinutos: Int,
    val dataHora: Long,
    val observacao: String = ""
)
