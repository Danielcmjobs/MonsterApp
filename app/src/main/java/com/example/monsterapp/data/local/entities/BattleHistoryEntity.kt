package com.example.monsterapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * [MEJORA] Entidad que almacena el historial de combates
 * Permite saber cuántas veces ganaste/perdiste contra cada Pokémon
 */
@Entity(tableName = "battle_history")
data class BattleHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val playerPokemonId: Int,      // ID del Pokémon del jugador
    val playerPokemonName: String, // Nombre del Pokémon del jugador
    val playerPokemonLevel: Int,   // Nivel del Pokémon del jugador

    val opponentPokemonId: Int,      // ID del Pokémon oponente
    val opponentPokemonName: String, // Nombre del Pokémon oponente
    val opponentPokemonLevel: Int,   // Nivel del Pokémon oponente

    val result: String,  // "WIN" o "LOSE"
    val damageDealt: Int,    // Daño que hiciste
    val damageReceived: Int, // Daño que recibiste
    val battleDate: Long,    // Timestamp del combate
    val durationSeconds: Int // Duración en segundos
)

