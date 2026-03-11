package com.example.monsterapp.data.local.dao

import androidx.room.*
import com.example.monsterapp.data.local.entities.PokemonEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para la tabla de Pokémon capturados.
 *
 * Define las operaciones CRUD (Create, Read, Update, Delete) disponibles.
 * Room genera automáticamente la implementación de esta interfaz.
 *
 * Usamos:
 * - suspend fun: Para operaciones que modifican datos (requieren corrutinas)
 * - Flow: Para consultas reactivas que notifican cambios automáticamente
 */
@Dao
interface PokemonDao {

    /**
     * Inserta un nuevo Pokémon capturado en la base de datos.
     * Si ya existe uno con el mismo ID, lo reemplaza.
     *
     * @param pokemon El Pokémon a insertar
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPokemon(pokemon: PokemonEntity)

    /**
     * Obtiene todos los Pokémon capturados, ordenados por fecha de captura (más recientes primero).
     *
     * Al devolver Flow, Room notificará automáticamente cualquier cambio a la UI.
     * Esto significa que cuando captures o liberes un Pokémon, la lista se actualizará sola.
     *
     * @return Flow con la lista de Pokémon capturados
     */
    @Query("SELECT * FROM pokemon_capturados ORDER BY fechaCaptura DESC")
    fun obtenerTodosLosPokemon(): Flow<List<PokemonEntity>>

    /**
     * Obtiene un Pokémon específico por su ID de Room.
     *
     * @param id ID único del Pokémon en la base de datos
     * @return El Pokémon encontrado o null si no existe
     */
    @Query("SELECT * FROM pokemon_capturados WHERE id = :id")
    suspend fun obtenerPokemonPorId(id: Int): PokemonEntity?

    /**
     * [MEJORA] Obtiene un Pokémon por su ID de la API (pokemonId).
     * Útil para cargar el nivel real de un Pokémon capturado.
     *
     * @param apiId ID del Pokémon en la API (ej: 25 para Pikachu)
     * @return El Pokémon encontrado o null si no existe
     */
    @Query("SELECT * FROM pokemon_capturados WHERE pokemonId = :apiId LIMIT 1")
    suspend fun obtenerPokemonPorApiId(apiId: Int): PokemonEntity?

    /**
     * Obtiene el número total de Pokémon capturados.
     *
     * @return Flow con el conteo (se actualiza automáticamente)
     */
    @Query("SELECT COUNT(*) FROM pokemon_capturados")
    fun contarPokemonCapturados(): Flow<Int>

    /**
     * Elimina (libera) un Pokémon de la base de datos.
     *
     * @param pokemon El Pokémon a eliminar
     */
    @Delete
    suspend fun liberarPokemon(pokemon: PokemonEntity)

    /**
     * [MEJORA] Actualiza el nivel de un Pokémon después de ganar un combate.
     *
     * @param pokemonId ID de la API del Pokémon
     * @param nuevoNivel Nuevo nivel del Pokémon
     */
    @Query("UPDATE pokemon_capturados SET nivel = :nuevoNivel WHERE pokemonId = :pokemonId")
    suspend fun actualizarNivel(pokemonId: Int, nuevoNivel: Int)

    /**
     * [MEJORA] Marca un Pokémon como herido (perdió un combate).
     *
     * @param pokemonId ID de la API del Pokémon
     */
    @Query("UPDATE pokemon_capturados SET estaHerido = 1 WHERE pokemonId = :pokemonId")
    suspend fun marcarComoHerido(pokemonId: Int)

    /**
     * [MEJORA] Cura un Pokémon (ya no está herido).
     *
     * @param pokemonId ID de la API del Pokémon
     */
    @Query("UPDATE pokemon_capturados SET estaHerido = 0 WHERE pokemonId = :pokemonId")
    suspend fun curarPokemon(pokemonId: Int)

    /**
     * [MEJORA] Obtiene la cantidad de Pokémon heridos.
     */
    @Query("SELECT COUNT(*) FROM pokemon_capturados WHERE estaHerido = 1")
    fun contarPokemonHeridos(): Flow<Int>

    /**
     * Elimina todos los Pokémon capturados (limpiar Pokédex).
     */
    @Query("DELETE FROM pokemon_capturados")
    suspend fun liberarTodos()
}

