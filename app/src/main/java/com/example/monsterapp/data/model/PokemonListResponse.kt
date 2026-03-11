package com.example.monsterapp.data.model

import com.squareup.moshi.Json

// Respuesta de la API para la lista de Pokémon
// Contiene una lista de resultados con nombre y URL
data class PokemonListResponse(
    @Json(name = "results")
    val results: List<PokemonResult>
)
