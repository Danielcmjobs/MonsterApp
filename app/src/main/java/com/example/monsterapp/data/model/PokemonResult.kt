package com.example.monsterapp.data.model

import com.squareup.moshi.Json

/**
 * Modelo que representa cada Pokémon en la lista de resultados.
 * Contiene el nombre del Pokémon y su URL para obtener más detalles.
 */
data class PokemonResult(
    // Nombre del Pokémon (ej: "bulbasaur", "charmander")
    @Json(name = "name")
    val name: String,

    // URL para obtener los detalles completos del Pokémon
    @Json(name = "url")
    val url: String
)

