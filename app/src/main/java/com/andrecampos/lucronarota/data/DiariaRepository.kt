package com.andrecampos.lucronarota.data

import com.andrecampos.lucronarota.util.Calculadora
import kotlinx.coroutines.flow.Flow

class DiariaRepository(
    private val diariaDao: DiariaDao,
    private val veiculoDao: VeiculoDao
) {
    fun observarDiariaAberta(): Flow<Diaria?> = diariaDao.observarAberta()

    fun observarFinalizadas(): Flow<List<Diaria>> = diariaDao.observarFinalizadas()

    fun observarFinalizadasDesde(inicio: Long): Flow<List<Diaria>> =
        diariaDao.observarFinalizadasDesde(inicio)

    fun observarVeiculos(): Flow<List<Veiculo>> = veiculoDao.observarTodos()

    suspend fun iniciarDiaria(veiculo: Veiculo, kmInicial: Double, observacao: String = ""): Long {
        val diaria = Diaria(
            veiculoId = veiculo.id,
            veiculoNome = veiculo.nome,
            custoPorKmSnapshot = Calculadora.custoPorKm(veiculo),
            custoFixoSnapshot = veiculo.custoFixoDiario,
            kmInicial = kmInicial,
            inicioEm = System.currentTimeMillis(),
            observacao = observacao
        )
        return diariaDao.inserir(diaria)
    }

    suspend fun finalizarDiaria(
        diaria: Diaria,
        kmFinal: Double,
        ganho: Double,
        observacao: String
    ): Diaria {
        val atualizada = diaria.copy(
            kmFinal = kmFinal,
            ganho = ganho,
            fimEm = System.currentTimeMillis(),
            observacao = observacao,
            finalizada = true
        )
        diariaDao.atualizar(atualizada)
        return atualizada
    }

    suspend fun removerDiaria(diaria: Diaria) = diariaDao.remover(diaria)

    suspend fun salvarVeiculo(veiculo: Veiculo): Long = veiculoDao.inserir(veiculo)

    suspend fun atualizarVeiculo(veiculo: Veiculo) = veiculoDao.atualizar(veiculo)

    suspend fun removerVeiculo(veiculo: Veiculo) = veiculoDao.remover(veiculo)
}
