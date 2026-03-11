package com.example.monsterapp.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PokemonDetail(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "height") val height: Int,
    @Json(name = "weight") val weight: Int,
    @Json(name = "base_experience") val baseExperience: Int?,
    @Json(name = "sprites") val sprites: Sprites,
    @Json(name = "types") val types: List<TypeSlot>,
    @Json(name = "stats") val stats: List<StatSlot>,
    @Json(name = "abilities") val abilities: List<AbilitySlot>
)

@JsonClass(generateAdapter = true)
data class Sprites(
    @Json(name = "front_default") val frontDefault: String?,
    @Json(name = "other") val other: OtherSprites?
)

@JsonClass(generateAdapter = true)
data class OtherSprites(
    @Json(name = "official-artwork") val officialArtwork: OfficialArtwork?
)

@JsonClass(generateAdapter = true)
data class OfficialArtwork(
    @Json(name = "front_default") val frontDefault: String?
)

@JsonClass(generateAdapter = true)
data class TypeSlot(
    @Json(name = "slot") val slot: Int,
    @Json(name = "type") val type: NamedResource
)

@JsonClass(generateAdapter = true)
data class StatSlot(
    @Json(name = "base_stat") val baseStat: Int,
    @Json(name = "effort") val effort: Int,
    @Json(name = "stat") val stat: NamedResource
)

@JsonClass(generateAdapter = true)
data class AbilitySlot(
    @Json(name = "is_hidden") val isHidden: Boolean,
    @Json(name = "slot") val slot: Int,
    @Json(name = "ability") val ability: NamedResource
)

@JsonClass(generateAdapter = true)
data class NamedResource(
    @Json(name = "name") val name: String,
    @Json(name = "url") val url: String
)
