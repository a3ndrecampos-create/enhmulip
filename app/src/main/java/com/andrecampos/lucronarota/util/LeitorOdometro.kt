package com.andrecampos.lucronarota.util

/**
 * Extrai um número de km plausível a partir do texto reconhecido pela câmera
 * no painel do carro. Odômetros normalmente têm entre 3 e 7 dígitos, então
 * escolhemos a maior sequência de dígitos contínua encontrada no texto.
 */
object LeitorOdometro {
    private val padraoDigitos = Regex("\\d{3,7}")

    fun extrairKm(textoReconhecido: String): String? {
        val candidatos = padraoDigitos.findAll(textoReconhecido).map { it.value }.toList()
        return candidatos.maxByOrNull { it.length }
    }
}
