package com.example.monsterapp.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PokemonListResponse(
    @Json(name = "count") val count: Int,
    @Json(name = "next") val next: String?,
    @Json(name = "previous") val previous: String?,
    @Json(name = "results") val results: List<PokemonEntry>
)

@JsonClass(generateAdapter = true)
data class PokemonEntry(
    @Json(name = "name") val name: String,
    @Json(name = "url") val url: String
) {
    val id: Int
        get() = url.trimEnd('/').split('/').last().toIntOrNull() ?: 0

    val imageUrl: String
        get() = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
}
