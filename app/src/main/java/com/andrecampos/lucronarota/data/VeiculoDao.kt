package com.andrecampos.lucronarota.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VeiculoDao {

    @Query("SELECT * FROM veiculos ORDER BY nome ASC")
    fun observarTodos(): Flow<List<Veiculo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(veiculo: Veiculo): Long

    @Update
    suspend fun atualizar(veiculo: Veiculo)

    @Delete
    suspend fun remover(veiculo: Veiculo)
}
