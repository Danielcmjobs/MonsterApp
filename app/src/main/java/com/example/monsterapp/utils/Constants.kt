package com.example.monsterapp.utils

/**
 * Archivo de constantes globales de la aplicación.
 * Aquí se pueden añadir clases de utilidad como:
 * - Extensiones de Kotlin
 * - Constantes de la app
 * - Helpers para formateo, validaciones, etc.
 */

object Constants {
    const val TAG = "MonsterApp"
    const val DEFAULT_POKEMON_LIMIT = 20

    // URLs de la API
    const val POKEAPI_BASE_URL = "https://pokeapi.co/api/v2/"
    const val POKEMON_IMAGE_URL = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/"

    // Tiempos
    const val LOADING_DELAY_MS = 3000L
    const val ANIMATION_DURATION_MS = 300L
}

