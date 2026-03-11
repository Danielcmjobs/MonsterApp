package com.example.monsterapp.utils

import com.example.monsterapp.data.local.entities.PokemonEntity

/**
 * [MEJORA] Utilidades para calcular estadísticas de Pokémon
 * No forma parte de la práctica, es un añadido para mejorar UX
 */
object StatsUtils {

    data class PokemonStats(
        val totalCapturados: Int,
        val nivelPromedio: Float,
        val nivelMasAlto: Int,
        val nivelMasBajo: Int,
        val nombreMasFuerte: String
    )

    /**
     * Calcula estadísticas completas de la colección de Pokémon.
     *
     * @param pokemonList Lista de Pokémon capturados
     * @return Objeto con las estadísticas calculadas
     */
    fun calcularEstadisticas(pokemonList: List<PokemonEntity>): PokemonStats {
        return if (pokemonList.isEmpty()) {
            PokemonStats(
                totalCapturados = 0,
                nivelPromedio = 0f,
                nivelMasAlto = 0,
                nivelMasBajo = 0,
                nombreMasFuerte = "N/A"
            )
        } else {
            val nivelPromedio = pokemonList.map { it.nivel }.average().toFloat()
            val nivelMasAlto = pokemonList.maxOf { it.nivel }
            val nivelMasBajo = pokemonList.minOf { it.nivel }
            val masFuerte = pokemonList.maxByOrNull { it.nivel }

            PokemonStats(
                totalCapturados = pokemonList.size,
                nivelPromedio = nivelPromedio,
                nivelMasAlto = nivelMasAlto,
                nivelMasBajo = nivelMasBajo,
                nombreMasFuerte = masFuerte?.nombre?.replaceFirstChar { it.uppercase() } ?: "N/A"
            )
        }
    }

    /**
     * Calcula el porcentaje de captura respecto al total disponible.
     */
    fun calcularPorcentajeCaptura(capturados: Int, totalDisponible: Int): Float {
        return if (totalDisponible == 0) 0f else (capturados.toFloat() / totalDisponible) * 100
    }
}

