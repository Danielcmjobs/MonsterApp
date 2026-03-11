package com.example.monsterapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.monsterapp.data.local.entities.PokemonEntity
import com.example.monsterapp.data.model.PokemonDetail
import com.example.monsterapp.data.repository.PokemonRepository
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de detalles del Pokémon.
 * Carga los detalles completos desde la API.
 */
class DetailsViewModel(private val repository: PokemonRepository) : ViewModel() {

    private val TAG = "DetailsViewModel"

    // Estado de carga
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Detalles del Pokémon
    private val _pokemonDetail = MutableLiveData<PokemonDetail>()
    val pokemonDetail: LiveData<PokemonDetail> = _pokemonDetail

    // [MEJORA] Entidad del Pokémon desde Room (con nivel real)
    private val _pokemonEntity = MutableLiveData<PokemonEntity?>()
    val pokemonEntity: LiveData<PokemonEntity?> = _pokemonEntity

    // Errores
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    /**
     * Carga los detalles de un Pokémon específico.
     *
     * @param pokemonId ID del Pokémon a cargar
     */
    fun loadPokemonDetail(pokemonId: Int) {
        Log.d(TAG, "📋 Cargando detalles del Pokémon #$pokemonId...")

        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val detail = repository.getPokemonDetail(pokemonId)
                _pokemonDetail.value = detail
                Log.d(TAG, "✅ Detalles cargados: ${detail.name}")
            } catch (e: Exception) {
                val errorMsg = "Error al cargar detalles: ${e.message}"
                Log.e(TAG, "❌ $errorMsg")
                _error.value = errorMsg
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * [MEJORA] Carga el Pokémon desde Room para obtener el nivel real.
     *
     * @param pokemonId ID del Pokémon (API ID)
     */
    fun cargarPokemonDesdeRoom(pokemonId: Int) {
        viewModelScope.launch {
            try {
                val entity = repository.obtenerPokemonPorApiId(pokemonId)
                if (entity != null) {
                    Log.d(TAG, "📦 Pokémon encontrado en Room: ${entity.nombre} Lv.${entity.nivel}, Herido: ${entity.estaHerido}")
                    _pokemonEntity.postValue(entity)
                } else {
                    Log.d(TAG, "⚠️ Pokémon no encontrado en Room, usando nivel aleatorio")
                    _pokemonEntity.postValue(null)
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al cargar desde Room: ${e.message}")
                _pokemonEntity.postValue(null)
            }
        }
    }

    /**
     * [MEJORA] Cantidad de curas disponibles.
     */
    val cantidadCuras: LiveData<Int?> = repository.obtenerCantidadCuras()
        ?.asLiveData()
        ?: MutableLiveData(0)

    /**
     * [MEJORA] Cura un Pokémon usando una poción.
     *
     * @param pokemonId ID del Pokémon a curar
     * @param onResult Callback con el resultado (true = curado, false = sin pociones)
     */
    fun curarPokemon(pokemonId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val exito = repository.curarPokemon(pokemonId)
            Log.d(TAG, if (exito) "💊 Pokémon curado" else "❌ No hay pociones")
            onResult(exito)
        }
    }

    /**
     * Limpia el mensaje de error.
     */
    fun clearError() {
        _error.value = null
    }
}

