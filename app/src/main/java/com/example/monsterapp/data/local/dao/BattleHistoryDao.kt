package com.example.monsterapp.data.local.dao

import androidx.room.*
import com.example.monsterapp.data.local.entities.BattleHistoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * [MEJORA] DAO para operaciones con el historial de combates
 */
@Dao
interface BattleHistoryDao {

    /**
     * Inserta un nuevo registro de combate
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCombate(batalla: BattleHistoryEntity)

    /**
     * Obtiene todos los combates registrados
     */
    @Query("SELECT * FROM battle_history ORDER BY battleDate DESC")
    fun obtenerTodosCombates(): Flow<List<BattleHistoryEntity>>

    /**
     * Obtiene combates de un Pokémon específico
     */
    @Query("SELECT * FROM battle_history WHERE playerPokemonId = :pokemonId ORDER BY battleDate DESC")
    fun obtenerCombatesPorPokemon(pokemonId: Int): Flow<List<BattleHistoryEntity>>

    /**
     * Obtiene estadísticas de victorias/derrotas
     */
    @Query("SELECT COUNT(*) FROM battle_history WHERE result = 'WIN'")
    fun contarVictorias(): Flow<Int>

    @Query("SELECT COUNT(*) FROM battle_history WHERE result = 'LOSE'")
    fun contarDerrotas(): Flow<Int>

    /**
     * Elimina un combate específico
     */
    @Delete
    suspend fun eliminarCombate(batalla: BattleHistoryEntity)
}

