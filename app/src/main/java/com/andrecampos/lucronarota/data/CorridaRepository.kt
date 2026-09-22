package com.andrecampos.lucronarota.data

import kotlinx.coroutines.flow.Flow

class CorridaRepository(
    private val corridaDao: CorridaDao,
    private val configDao: ConfigDao
) {
    fun observarCorridas(): Flow<List<Corrida>> = corridaDao.observarTodas()

    fun observarCorridasDesde(inicio: Long): Flow<List<Corrida>> =
        corridaDao.observarDesde(inicio)

    fun observarConfig(): Flow<ConfigVeiculo?> = configDao.observar()

    suspend fun salvarCorrida(corrida: Corrida): Long = corridaDao.inserir(corrida)

    suspend fun atualizarCorrida(corrida: Corrida) = corridaDao.atualizar(corrida)

    suspend fun removerCorrida(corrida: Corrida) = corridaDao.remover(corrida)

    suspend fun salvarConfig(config: ConfigVeiculo) = configDao.salvar(config)
}
