package com.andrecampos.lucronarota.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AbastecimentoDao {

    @Query("SELECT * FROM abastecimentos WHERE diariaId = :diariaId ORDER BY registradoEm DESC")
    fun observarPorDiaria(diariaId: Long): Flow<List<Abastecimento>>

    @Insert
    suspend fun inserir(abastecimento: Abastecimento): Long

    @Delete
    suspend fun remover(abastecimento: Abastecimento)

    @Query("SELECT COALESCE(SUM(valor), 0.0) FROM abastecimentos WHERE diariaId = :diariaId")
    suspend fun somarPorDiaria(diariaId: Long): Double
}
