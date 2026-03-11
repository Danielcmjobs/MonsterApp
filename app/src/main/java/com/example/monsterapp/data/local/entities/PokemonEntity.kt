package com.example.monsterapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad de Room que representa un Pokémon capturado.
 *
 * Esta clase define la estructura de la tabla "pokemon_capturados" en la base de datos.
 * Cada instancia de PokemonEntity representa una fila en la tabla.
 *
 * @property id ID único autogenerado por Room (clave primaria)
 * @property pokemonId ID original de la PokeAPI (para recuperar la imagen)
 * @property nombre Nombre del Pokémon
 * @property nivel Nivel del Pokémon (generado aleatoriamente al capturar)
 * @property fechaCaptura Timestamp de cuando fue capturado (en milisegundos)
 * @property latitud Coordenada de latitud donde fue capturado
 * @property longitud Coordenada de longitud donde fue capturado
 * @property estaHerido [MEJORA] Indica si el Pokémon está herido (perdió un combate)
 */
@Entity(tableName = "pokemon_capturados")
data class PokemonEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,  // ID único autogenerado por Room

    val pokemonId: Int,      // ID original de la PokeAPI (ej: 1 para Bulbasaur)
    val nombre: String,      // Nombre del Pokémon
    val nivel: Int,          // Nivel del Pokémon (1-100)
    val fechaCaptura: Long,  // Timestamp en milisegundos

    // Coordenadas donde fue capturado (para mostrar en el mapa)
    val latitud: Double,
    val longitud: Double,

    // [MEJORA] Estado de salud del Pokémon
    val estaHerido: Boolean = false  // true = herido (perdió combate), false = sano
)

