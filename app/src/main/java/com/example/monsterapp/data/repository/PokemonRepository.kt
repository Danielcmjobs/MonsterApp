package com.example.monsterapp.data.repository

import android.util.Log
import com.example.monsterapp.data.local.dao.BattleHistoryDao
import com.example.monsterapp.data.local.dao.InventarioDao
import com.example.monsterapp.data.local.dao.PokemonDao
import com.example.monsterapp.data.local.entities.BattleHistoryEntity
import com.example.monsterapp.data.local.entities.InventarioEntity
import com.example.monsterapp.data.local.entities.PokemonEntity
import com.example.monsterapp.data.model.PokemonListResponse
import com.example.monsterapp.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio que actúa como único punto de acceso a los datos.
 *
 * Combina dos fuentes de datos:
 * - REMOTA (API): Obtiene lista de Pokémon desde PokeAPI via Retrofit
 * - LOCAL (Room): Guarda y recupera Pokémon capturados
 *
 * Ventajas del patrón Repository:
 * - Abstrae la fuente de datos (el ViewModel no sabe de dónde vienen los datos)
 * - Facilita los tests unitarios (se puede crear un mock del repositorio)
 * - Centraliza la lógica de acceso a datos
 *
 * @param pokemonDao DAO de Room para operaciones en la base de datos local
 * @param battleHistoryDao [MEJORA] DAO para historial de combates
 * @param inventarioDao [MEJORA] DAO para inventario (curas)
 */
class PokemonRepository(
    private val pokemonDao: PokemonDao,
    private val battleHistoryDao: BattleHistoryDao? = null,
    private val inventarioDao: InventarioDao? = null
) {

    private val TAG = "PokemonRepository"

    // ==================== PARTE REMOTA (API) ====================

    /**
     * Obtiene la lista de Pokémon desde la API de PokeAPI.
     *
     * @param limit Número de Pokémon a obtener
     * @return Respuesta con la lista de Pokémon
     */
    suspend fun getPokemonList(limit: Int): PokemonListResponse {
        Log.d(TAG, "🌐 Solicitando $limit Pokémon desde la API...")
        return RetrofitClient.api.getPokemonList(limit)
    }

    /**
     * Obtiene los detalles completos de un Pokémon específico desde la API.
     *
     * @param id ID del Pokémon
     * @return Detalles completos del Pokémon
     */
    suspend fun getPokemonDetail(id: Int) = RetrofitClient.api.getPokemonDetail(id)

    // ==================== PARTE LOCAL (ROOM) ====================

    /**
     * Guarda un Pokémon capturado en la base de datos local.
     *
     * @param pokemon Entidad del Pokémon a guardar
     */
    suspend fun capturarPokemon(pokemon: PokemonEntity) {
        Log.d(TAG, "💾 Guardando ${pokemon.nombre} en la base de datos...")
        pokemonDao.insertarPokemon(pokemon)
    }

    /**
     * Obtiene todos los Pokémon capturados como un Flow.
     *
     * Flow es reactivo: cualquier cambio en la BD se notifica automáticamente.
     * Esto significa que cuando captures o liberes un Pokémon, la UI se actualiza sola.
     */
    val misPokemonCapturados: Flow<List<PokemonEntity>> = pokemonDao.obtenerTodosLosPokemon()

    /**
     * Obtiene el número de Pokémon capturados como un Flow reactivo.
     */
    val contadorCapturados: Flow<Int> = pokemonDao.contarPokemonCapturados()

    /**
     * Libera (elimina) un Pokémon de la base de datos.
     *
     * @param pokemon Pokémon a liberar
     */
    suspend fun liberarPokemon(pokemon: PokemonEntity) {
        Log.d(TAG, "🔓 Liberando ${pokemon.nombre}...")
        pokemonDao.liberarPokemon(pokemon)
    }

    /**
     * Libera todos los Pokémon (limpia la Pokédex).
     */
    suspend fun liberarTodos() {
        Log.d(TAG, "🔓 Liberando todos los Pokémon...")
        pokemonDao.liberarTodos()
    }

    /**
     * Obtiene un Pokémon específico por su ID.
     *
     * @param id ID del Pokémon en la BD
     * @return Pokémon o null si no existe
     */
    suspend fun obtenerPokemonPorId(id: Int): PokemonEntity? {
        return pokemonDao.obtenerPokemonPorId(id)
    }

    /**
     * [MEJORA] Obtiene un Pokémon por su ID de la API (pokemonId).
     *
     * @param apiId ID del Pokémon en la API (ej: 25 para Pikachu)
     * @return Pokémon o null si no existe
     */
    suspend fun obtenerPokemonPorApiId(apiId: Int): PokemonEntity? {
        return pokemonDao.obtenerPokemonPorApiId(apiId)
    }

    /**
     * [MEJORA] Sube el nivel de un Pokémon después de ganar un combate.
     *
     * @param pokemonId ID de la API del Pokémon
     * @param nivelesGanados Cantidad de niveles a subir
     * @return Nuevo nivel del Pokémon
     */
    suspend fun subirNivel(pokemonId: Int, nivelesGanados: Int): Int {
        val pokemon = pokemonDao.obtenerPokemonPorApiId(pokemonId)
        if (pokemon != null) {
            val nuevoNivel = (pokemon.nivel + nivelesGanados).coerceAtMost(100)
            pokemonDao.actualizarNivel(pokemonId, nuevoNivel)
            Log.d(TAG, "⬆️ ${pokemon.nombre} subió de nivel: ${pokemon.nivel} → $nuevoNivel")
            return nuevoNivel
        }
        return 0
    }

    // ==================== HISTORIAL DE COMBATES ====================

    /**
     * [MEJORA] Guarda un registro de combate en el historial
     */
    suspend fun guardarHistorialCombate(batalla: BattleHistoryEntity) {
        if (battleHistoryDao != null) {
            Log.d(TAG, "⚔️ Guardando combate en historial...")
            battleHistoryDao.insertarCombate(batalla)
        }
    }

    /**
     * [MEJORA] Obtiene el historial de combates
     */
    fun obtenerHistorialCombates(): Flow<List<BattleHistoryEntity>>? {
        return if (battleHistoryDao != null) {
            Log.d(TAG, "📊 Obteniendo historial de combates...")
            battleHistoryDao.obtenerTodosCombates()
        } else {
            null
        }
    }

    /**
     * [MEJORA] Obtiene victorias totales
     */
    fun contarVictorias(): Flow<Int>? {
        return battleHistoryDao?.contarVictorias()
    }

    /**
     * [MEJORA] Obtiene derrotas totales
     */
    fun contarDerrotas(): Flow<Int>? {
        return battleHistoryDao?.contarDerrotas()
    }

    // ==================== SISTEMA DE HERIDOS Y CURAS ====================

    /**
     * [MEJORA] Marca un Pokémon como herido (perdió un combate).
     */
    suspend fun marcarComoHerido(pokemonId: Int) {
        Log.d(TAG, "🤕 Marcando Pokémon #$pokemonId como herido")
        pokemonDao.marcarComoHerido(pokemonId)
    }

    /**
     * [MEJORA] Cura un Pokémon usando una poción del inventario.
     * @return true si se curó exitosamente, false si no hay curas
     */
    suspend fun curarPokemon(pokemonId: Int): Boolean {
        val inventario = inventarioDao?.obtenerInventarioSync()
        if (inventario == null || inventario.cantidadCuras <= 0) {
            Log.w(TAG, "❌ No hay curas disponibles")
            return false
        }

        // Usar una cura y curar al Pokémon
        inventarioDao.usarCura()
        pokemonDao.curarPokemon(pokemonId)
        Log.d(TAG, "💊 Pokémon #$pokemonId curado. Curas restantes: ${inventario.cantidadCuras - 1}")
        return true
    }

    /**
     * [MEJORA] Obtiene la cantidad de curas disponibles.
     */
    fun obtenerCantidadCuras(): Flow<Int?>? {
        return inventarioDao?.obtenerCantidadCuras()
    }

    /**
     * [MEJORA] Añade curas al inventario (cuando se recoge del mapa).
     */
    suspend fun añadirCuras(cantidad: Int) {
        // Asegurar que el inventario existe
        inventarioDao?.inicializarInventario()
        inventarioDao?.añadirCuras(cantidad)
        Log.d(TAG, "💊 +$cantidad curas añadidas al inventario")
    }

    /**
     * [MEJORA] Inicializa el inventario si no existe.
     */
    suspend fun inicializarInventario() {
        inventarioDao?.inicializarInventario()
    }

    /**
     * [MEJORA] Obtiene la cantidad de Pokémon heridos.
     */
    fun contarPokemonHeridos(): Flow<Int> {
        return pokemonDao.contarPokemonHeridos()
    }
}
