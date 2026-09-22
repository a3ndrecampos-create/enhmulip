package com.andrecampos.lucronarota.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfigDao {

    @Query("SELECT * FROM config_veiculo WHERE id = 1")
    fun observar(): Flow<ConfigVeiculo?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun salvar(config: ConfigVeiculo)
}
