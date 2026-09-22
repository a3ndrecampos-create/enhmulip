package com.andrecampos.lucronarota.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DiariaDao {

    @Query("SELECT * FROM diarias WHERE finalizada = 0 ORDER BY inicioEm DESC LIMIT 1")
    fun observarAberta(): Flow<Diaria?>

    @Query("SELECT * FROM diarias WHERE finalizada = 1 ORDER BY fimEm DESC")
    fun observarFinalizadas(): Flow<List<Diaria>>

    @Query("SELECT * FROM diarias WHERE finalizada = 1 AND fimEm >= :inicio ORDER BY fimEm DESC")
    fun observarFinalizadasDesde(inicio: Long): Flow<List<Diaria>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(diaria: Diaria): Long

    @Update
    suspend fun atualizar(diaria: Diaria)

    @Delete
    suspend fun remover(diaria: Diaria)
}
