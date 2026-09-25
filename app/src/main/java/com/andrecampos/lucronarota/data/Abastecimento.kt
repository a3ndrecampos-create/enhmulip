package com.andrecampos.lucronarota.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Um abastecimento feito durante uma diária. O preço do combustível muda de
 * posto pra posto, então registramos o valor real gasto em vez de confiar
 * só na média configurada no veículo.
 */
@Entity(tableName = "abastecimentos")
data class Abastecimento(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val diariaId: Long,
    val valor: Double,
    val litros: Double? = null,
    val local: String = "",
    val registradoEm: Long = System.currentTimeMillis()
)
