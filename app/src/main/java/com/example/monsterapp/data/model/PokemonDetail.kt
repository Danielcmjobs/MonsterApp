package com.example.monsterapp.data.model

import com.squareup.moshi.Json

/**
 * Modelo completo de un Pokémon con todos sus detalles.
 * Se obtiene desde el endpoint: https://pokeapi.co/api/v2/pokemon/{id}
 */
data class PokemonDetail(
    @Json(name = "id")
    val id: Int,

    @Json(name = "name")
    val name: String,

    @Json(name = "height")
    val height: Int,  // Altura en decímetros

    @Json(name = "weight")
    val weight: Int,  // Peso en hectogramos

    @Json(name = "types")
    val types: List<PokemonTypeSlot>,

    @Json(name = "stats")
    val stats: List<PokemonStat>,

    @Json(name = "sprites")
    val sprites: PokemonSprites
)

/**
 * Slot de tipo de Pokémon (un Pokémon puede tener 1 o 2 tipos)
 */
data class PokemonTypeSlot(
    @Json(name = "slot")
    val slot: Int,

    @Json(name = "type")
    val type: PokemonType
)

/**
 * Información del tipo
 */
data class PokemonType(
    @Json(name = "name")
    val name: String  // Ejemplo: "grass", "poison"
)

/**
 * Estadística individual del Pokémon
 */
data class PokemonStat(
    @Json(name = "base_stat")
    val baseStat: Int,

    @Json(name = "stat")
    val stat: Stat
)

/**
 * Nombre de la estadística
 */
data class Stat(
    @Json(name = "name")
    val name: String  // Ejemplo: "hp", "attack", "defense"
)

/**
 * Sprites (imágenes) del Pokémon
 */
data class PokemonSprites(
    @Json(name = "front_default")
    val frontDefault: String?,

    @Json(name = "other")
    val other: OtherSprites?
)

/**
 * Sprites alternativos de mejor calidad
 */
data class OtherSprites(
    @Json(name = "official-artwork")
    val officialArtwork: OfficialArtwork?
)

/**
 * Artwork oficial (imagen de alta calidad)
 */
data class OfficialArtwork(
    @Json(name = "front_default")
    val frontDefault: String?
)

