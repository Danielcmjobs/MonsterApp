package com.example.monsterapp.domain.battle

import kotlin.math.pow
import kotlin.random.Random

/**
 * [MEJORA] Servicio que contiene toda la lógica de cálculo de combate
 * - Daño
 * - Efectividad de tipos
 * - Críticos
 * - IA del oponente
 */
object BattleCalculator {

    /**
     * Calcula el daño base de un ataque
     * Fórmula simplificada: (((2 * Level / 5 + 2) * Attack * Power / Defense) / 50) + 2
     */
    fun calcularDano(
        nivelAtacante: Int,
        ataqueAtacante: Int,
        defensorOponente: Int,
        poderAtaque: Int = 100
    ): Int {
        val danio = ((2 * nivelAtacante / 5 + 2) * ataqueAtacante * poderAtaque / defensorOponente) / 50 + 2
        return danio.coerceIn(1, 999)
    }

    /**
     * Calcula la efectividad según el tipo del ataque vs defensor
     * Tabla simplificada de Pokémon
     */
    fun calcularEfectividad(tipoAtaque: String, tipoDefensor: String): Float {
        return when {
            // Super efectivo (2x)
            tipoAtaque == "fire" && tipoDefensor in listOf("grass", "bug", "steel") -> 2.0f
            tipoAtaque == "water" && tipoDefensor in listOf("fire", "ground", "rock") -> 2.0f
            tipoAtaque == "electric" && tipoDefensor == "water" -> 2.0f
            tipoAtaque == "grass" && tipoDefensor in listOf("water", "ground", "rock") -> 2.0f
            tipoAtaque == "ice" && tipoDefensor in listOf("flying", "ground", "grass", "dragon") -> 2.0f
            tipoAtaque == "fighting" && tipoDefensor in listOf("normal", "ice", "rock", "dark", "steel") -> 2.0f
            tipoAtaque == "poison" && tipoDefensor == "grass" -> 2.0f
            tipoAtaque == "ground" && tipoDefensor in listOf("fire", "electric", "poison", "rock", "steel") -> 2.0f
            tipoAtaque == "flying" && tipoDefensor in listOf("fighting", "bug", "grass") -> 2.0f
            tipoAtaque == "psychic" && tipoDefensor in listOf("fighting", "poison") -> 2.0f
            tipoAtaque == "bug" && tipoDefensor in listOf("grass", "psychic", "dark") -> 2.0f
            tipoAtaque == "rock" && tipoDefensor in listOf("flying", "bug", "fire", "ice") -> 2.0f
            tipoAtaque == "ghost" && tipoDefensor in listOf("ghost", "psychic") -> 2.0f
            tipoAtaque == "dragon" && tipoDefensor == "dragon" -> 2.0f
            tipoAtaque == "dark" && tipoDefensor in listOf("ghost", "psychic") -> 2.0f
            tipoAtaque == "steel" && tipoDefensor in listOf("ice", "rock", "fairy") -> 2.0f
            tipoAtaque == "fairy" && tipoDefensor in listOf("fighting", "dragon", "dark") -> 2.0f

            // Poco efectivo (0.5x)
            tipoAtaque == "fire" && tipoDefensor in listOf("fire", "water", "grass", "ice", "bug", "steel", "fairy") -> 0.5f
            tipoAtaque == "water" && tipoDefensor in listOf("water", "grass", "dragon") -> 0.5f
            tipoAtaque == "electric" && tipoDefensor in listOf("electric", "grass", "dragon") -> 0.5f
            tipoAtaque == "grass" && tipoDefensor in listOf("fire", "grass", "poison", "flying", "bug", "dragon", "steel") -> 0.5f
            tipoAtaque == "ice" && tipoDefensor in listOf("fire", "water", "ice", "steel") -> 0.5f
            tipoAtaque == "fighting" && tipoDefensor in listOf("flying", "psychic", "fairy") -> 0.5f
            tipoAtaque == "poison" && tipoDefensor in listOf("poison", "ground", "rock", "ghost") -> 0.5f
            tipoAtaque == "ground" && tipoDefensor == "flying" -> 0.5f
            tipoAtaque == "flying" && tipoDefensor in listOf("electric", "rock", "steel") -> 0.5f
            tipoAtaque == "psychic" && tipoDefensor in listOf("psychic", "steel", "dark") -> 0.5f
            tipoAtaque == "bug" && tipoDefensor in listOf("fire", "fighting", "poison", "flying", "ghost", "steel", "fairy") -> 0.5f
            tipoAtaque == "rock" && tipoDefensor in listOf("fighting", "ground", "steel") -> 0.5f
            tipoAtaque == "ghost" && tipoDefensor == "dark" -> 0.5f
            tipoAtaque == "dragon" && tipoDefensor == "steel" -> 0.5f
            tipoAtaque == "dark" && tipoDefensor in listOf("dark", "steel", "fairy") -> 0.5f
            tipoAtaque == "steel" && tipoDefensor in listOf("fire", "water", "electric", "steel") -> 0.5f
            tipoAtaque == "fairy" && tipoDefensor in listOf("poison", "steel") -> 0.5f

            // Neutro
            else -> 1.0f
        }
    }

    /**
     * Determina si el ataque es crítico (10% de chance)
     */
    fun esAtaqueCritico(): Boolean = Random.nextInt(100) < 10

    /**
     * IA para decidir el movimiento del oponente
     * Estrategia simple: atacar siempre
     */
    fun decidirMovimientoOponente(estadoActual: BattleState): String {
        return "ATTACK"  // Por ahora siempre ataca
    }

    /**
     * Calcula quién ataca primero según velocidad
     */
    fun quienAtacaPrimero(velocidadPlayer: Int, velocidadOponente: Int): String {
        return if (velocidadPlayer >= velocidadOponente) "PLAYER" else "OPPONENT"
    }

    /**
     * Obtiene el tipo del Pokémon (primer tipo si tiene dos)
     */
    fun obtenerTipoPrimario(combatant: Combatant): String {
        return combatant.pokemonDetail.types.firstOrNull()?.type?.name ?: "normal"
    }
}

