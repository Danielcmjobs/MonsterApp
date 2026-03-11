package com.example.monsterapp.domain.battle

import com.example.monsterapp.data.local.entities.PokemonEntity
import com.example.monsterapp.data.model.PokemonDetail

/**
 * [MEJORA] Modelo de dominio para un combatiente en batalla
 * Contiene toda la información necesaria durante un combate
 */
data class Combatant(
    val pokemon: PokemonEntity,
    val pokemonDetail: PokemonDetail,
    val currentHP: Int,
    val maxHP: Int,
    val attack: Int,
    val defense: Int,
    val spAtk: Int,
    val spDef: Int,
    val speed: Int
) {
    /**
     * ¿Está derrotado?
     */
    fun isDefeated(): Boolean = currentHP <= 0

    /**
     * Calcula el daño que recibirá de un ataque
     */
    fun recibirDano(danioBase: Int, efectividad: Float): Int {
        val danioConDefensa = (danioBase * 100) / (defense * 2 + 100)
        val danioFinal = (danioConDefensa * efectividad).toInt().coerceAtLeast(1)
        return danioFinal
    }
}

/**
 * [MEJORA] Acción que ocurre en un combate
 */
data class BattleAction(
    val turno: Int,
    val atacante: String,  // "Player" o "Opponent"
    val accion: String,    // "Ataque", "Defensa", "Item"
    val danio: Int,
    val critico: Boolean,
    val efectividad: Float, // 0.5 (débil), 1.0 (normal), 2.0 (super efectivo)
    val descripcion: String
)

/**
 * [MEJORA] Estado actual de un combate
 */
data class BattleState(
    val playerPokemon: Combatant,
    val opponentPokemon: Combatant,
    val turno: Int = 1,
    val estado: String = "IN_PROGRESS",  // IN_PROGRESS, PLAYER_WIN, OPPONENT_WIN
    val acciones: List<BattleAction> = emptyList(),
    val mensajeActual: String = "¡Comienza el combate!"
) {
    /**
     * ¿El combate ha terminado?
     */
    fun haTerminado(): Boolean = playerPokemon.isDefeated() || opponentPokemon.isDefeated()

    /**
     * ¿Quién gana?
     */
    fun ganador(): String? = when {
        playerPokemon.isDefeated() -> "OPPONENT"
        opponentPokemon.isDefeated() -> "PLAYER"
        else -> null
    }
}

