package com.example.monsterapp.utils

import com.example.monsterapp.R

/**
 * [MEJORA OPCIONAL - No forma parte de la práctica]
 *
 * Utilidad para obtener colores según el tipo de Pokémon.
 * Mejora la presentación visual de los Pokémon mostrando
 * colores representativos de cada tipo.
 */
object PokemonTypeColors {

    /**
     * Devuelve el color de recurso correspondiente al tipo de Pokémon.
     *
     * @param tipo Nombre del tipo en español o inglés
     * @return ID del recurso de color
     */
    fun getColorForType(tipo: String): Int {
        return when (tipo.lowercase()) {
            // Tipos en inglés
            "normal" -> R.color.type_normal
            "fire", "fuego" -> R.color.type_fire
            "water", "agua" -> R.color.type_water
            "electric", "eléctrico" -> R.color.type_electric
            "grass", "planta" -> R.color.type_grass
            "ice", "hielo" -> R.color.type_ice
            "fighting", "lucha" -> R.color.type_fighting
            "poison", "veneno" -> R.color.type_poison
            "ground", "tierra" -> R.color.type_ground
            "flying", "volador" -> R.color.type_flying
            "psychic", "psíquico" -> R.color.type_psychic
            "bug", "bicho" -> R.color.type_bug
            "rock", "roca" -> R.color.type_rock
            "ghost", "fantasma" -> R.color.type_ghost
            "dragon", "dragón" -> R.color.type_dragon
            "dark", "siniestro" -> R.color.type_dark
            "steel", "acero" -> R.color.type_steel
            "fairy", "hada" -> R.color.type_fairy

            else -> R.color.type_normal
        }
    }
}

