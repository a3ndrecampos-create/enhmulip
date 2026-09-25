package com.andrecampos.lucronarota.data

import com.andrecampos.lucronarota.util.Calculadora
import kotlinx.coroutines.flow.Flow

class DiariaRepository(
    private val diariaDao: DiariaDao,
    private val veiculoDao: VeiculoDao,
    private val abastecimentoDao: AbastecimentoDao
) {
    fun observarDiariaAberta(): Flow<Diaria?> = diariaDao.observarAberta()

    fun observarFinalizadas(): Flow<List<Diaria>> = diariaDao.observarFinalizadas()

    fun observarFinalizadasDesde(inicio: Long): Flow<List<Diaria>> =
        diariaDao.observarFinalizadasDesde(inicio)

    fun observarVeiculos(): Flow<List<Veiculo>> = veiculoDao.observarTodos()

    fun observarAbastecimentos(diariaId: Long): Flow<List<Abastecimento>> =
        abastecimentoDao.observarPorDiaria(diariaId)

    suspend fun iniciarDiaria(veiculo: Veiculo, kmInicial: Double, observacao: String = ""): Long {
        val diaria = Diaria(
            veiculoId = veiculo.id,
            veiculoNome = veiculo.nome,
            custoCombustivelPorKmSnapshot = Calculadora.custoCombustivelPorKm(veiculo),
            custoOutrosPorKmSnapshot = veiculo.outrosCustosPorKm,
            custoFixoSnapshot = veiculo.custoFixoDiario,
            kmInicial = kmInicial,
            inicioEm = System.currentTimeMillis(),
            observacao = observacao
        )
        return diariaDao.inserir(diaria)
    }

    suspend fun registrarAbastecimento(diariaId: Long, valor: Double, litros: Double?, local: String) {
        abastecimentoDao.inserir(
            Abastecimento(diariaId = diariaId, valor = valor, litros = litros, local = local)
        )
    }

    suspend fun removerAbastecimento(abastecimento: Abastecimento) =
        abastecimentoDao.remover(abastecimento)

    suspend fun finalizarDiaria(
        diaria: Diaria,
        kmFinal: Double,
        ganho: Double,
        observacao: String
    ): Diaria {
        val totalAbastecido = abastecimentoDao.somarPorDiaria(diaria.id)
        val atualizada = diaria.copy(
            kmFinal = kmFinal,
            ganho = ganho,
            fimEm = System.currentTimeMillis(),
            observacao = observacao,
            custoCombustivelReal = totalAbastecido,
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
