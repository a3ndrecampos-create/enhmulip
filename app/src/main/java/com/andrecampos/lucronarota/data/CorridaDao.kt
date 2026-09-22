package com.andrecampos.lucronarota.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CorridaDao {

    @Query("SELECT * FROM corridas ORDER BY dataHora DESC")
    fun observarTodas(): Flow<List<Corrida>>

    @Query("SELECT * FROM corridas WHERE dataHora >= :inicio ORDER BY dataHora DESC")
    fun observarDesde(inicio: Long): Flow<List<Corrida>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(corrida: Corrida): Long

    @Update
    suspend fun atualizar(corrida: Corrida)

    @Delete
    suspend fun remover(corrida: Corrida)
}
