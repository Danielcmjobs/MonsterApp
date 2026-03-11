package com.example.monsterapp.utils

import com.example.monsterapp.data.local.entities.PokemonEntity

/**
 * [MEJORA] Extensiones de búsqueda y filtrado para Pokémon
 * No forma parte de la práctica, es un añadido para mejorar UX
 */
object SearchUtils {

    /**
     * Filtra una lista de Pokémon por nombre.
     *
     * @param pokemonList Lista original
     * @param searchText Texto a buscar (case-insensitive)
     * @return Lista filtrada
     */
    fun filterPokemonByName(pokemonList: List<PokemonEntity>, searchText: String): List<PokemonEntity> {
        if (searchText.isBlank()) return pokemonList

        return pokemonList.filter { pokemon ->
            pokemon.nombre.lowercase().contains(searchText.lowercase())
        }
    }

    /**
     * Filtra una lista de Pokémon por nivel.
     *
     * @param pokemonList Lista original
     * @param minLevel Nivel mínimo
     * @param maxLevel Nivel máximo
     * @return Lista filtrada
     */
    fun filterPokemonByLevel(
        pokemonList: List<PokemonEntity>,
        minLevel: Int,
        maxLevel: Int
    ): List<PokemonEntity> {
        return pokemonList.filter { pokemon ->
            pokemon.nivel in minLevel..maxLevel
        }
    }

    /**
     * Ordena Pokémon por fecha de captura (más recientes primero).
     */
    fun sortByDateDescending(pokemonList: List<PokemonEntity>): List<PokemonEntity> {
        return pokemonList.sortedByDescending { it.fechaCaptura }
    }

    /**
     * Ordena Pokémon por nivel (mayor primero).
     */
    fun sortByLevelDescending(pokemonList: List<PokemonEntity>): List<PokemonEntity> {
        return pokemonList.sortedByDescending { it.nivel }
    }

    /**
     * Ordena Pokémon alfabéticamente.
     */
    fun sortByNameAscending(pokemonList: List<PokemonEntity>): List<PokemonEntity> {
        return pokemonList.sortedBy { it.nombre }
    }
}

