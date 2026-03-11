package com.example.monsterapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monsterapp.data.local.entities.PokemonEntity
import com.example.monsterapp.data.model.PokemonResult
import com.example.monsterapp.data.repository.PokemonRepository
import kotlinx.coroutines.launch

/**
 * ViewModel para el MapFragment.
 *
 * Responsabilidades:
 * - Cargar Pokémon desde la API para mostrarlos en el mapa
 * - Capturar Pokémon (guardarlos en Room)
 *
 * Ahora recibe el repositorio por constructor (inyección de dependencias)
 * en lugar de crearlo internamente. Esto permite:
 * - Acceder tanto a la API como a Room
 * - Facilitar los tests unitarios
 *
 * @param repository Repositorio para acceso a datos (API + Room)
 */
class MapViewModel(private val repository: PokemonRepository) : ViewModel() {

    private val TAG = "MapViewModel"

    // LiveData para la lista de Pokémon de la API
    private val _pokemonList = MutableLiveData<List<PokemonResult>>()
    val pokemonList: LiveData<List<PokemonResult>> = _pokemonList

    // LiveData para estados de carga y errores
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // LiveData para notificar cuando un Pokémon es capturado
    private val _pokemonCapturado = MutableLiveData<String?>()
    val pokemonCapturado: LiveData<String?> = _pokemonCapturado

    /**
     * Carga Pokémon desde la API para mostrarlos en el mapa.
     *
     * @param limit Número de Pokémon a cargar (por defecto 20)
     */
    fun loadPokemon(limit: Int = 20) {
        Log.d(TAG, "📥 Cargando $limit Pokémon desde la API...")

        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val response = repository.getPokemonList(limit)
                _pokemonList.value = response.results
                Log.d(TAG, "✅ ${response.results.size} Pokémon cargados correctamente")

            } catch (e: Exception) {
                val errorMsg = e.message ?: "Error desconocido al cargar Pokémon"
                _error.value = errorMsg
                Log.e(TAG, "❌ Error: $errorMsg")

            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Captura un Pokémon y lo guarda en la base de datos Room.
     *
     * Proceso:
     * 1. Extrae el ID del Pokémon de su URL
     * 2. Crea la entidad PokemonEntity
     * 3. La guarda en Room a través del repositorio
     *
     * @param pokemon Datos del Pokémon de la API
     * @param lat Latitud donde fue capturado
     * @param lon Longitud donde fue capturado
     */
    fun capturarPokemon(pokemon: PokemonResult, lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                // 1. Extraer el ID de la URL (ej: "https://pokeapi.co/api/v2/pokemon/1/")
                val pokemonId = extraerIdDeUrl(pokemon.url)

                // 2. Crear la entidad para Room
                val nuevoPokemon = PokemonEntity(
                    pokemonId = pokemonId,
                    nombre = pokemon.name,
                    nivel = (1..100).random(),  // Nivel aleatorio para hacerlo divertido
                    fechaCaptura = System.currentTimeMillis(),
                    latitud = lat,
                    longitud = lon
                )

                // 3. Guardar en Room
                repository.capturarPokemon(nuevoPokemon)

                Log.d(TAG, "🎯 ¡${pokemon.name} capturado! (ID: $pokemonId, Nivel: ${nuevoPokemon.nivel})")

                // Notificar a la UI
                _pokemonCapturado.value = pokemon.name

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al capturar ${pokemon.name}: ${e.message}")
                _error.value = "Error al capturar ${pokemon.name}"
            }
        }
    }

    /**
     * Extrae el ID del Pokémon de su URL de la API.
     *
     * Ejemplo: "https://pokeapi.co/api/v2/pokemon/25/" → 25
     *
     * @param url URL del Pokémon
     * @return ID numérico del Pokémon
     */
    private fun extraerIdDeUrl(url: String): Int {
        return url.split("/")
            .filter { it.isNotEmpty() }
            .last()
            .toIntOrNull() ?: 0
    }

    /**
     * Limpia el mensaje de error.
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Limpia la notificación de captura.
     */
    fun clearCaptura() {
        _pokemonCapturado.value = null
    }

    /**
     * [MEJORA] Recoge una cura del mapa y la añade al inventario.
     */
    fun recogerCura() {
        viewModelScope.launch {
            try {
                repository.añadirCuras(1)
                Log.d(TAG, "💊 Cura recogida y añadida al inventario")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al recoger cura: ${e.message}")
            }
        }
    }
}

