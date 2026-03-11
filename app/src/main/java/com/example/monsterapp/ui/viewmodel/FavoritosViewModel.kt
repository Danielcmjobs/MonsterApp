package com.example.monsterapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.monsterapp.data.local.entities.PokemonEntity
import com.example.monsterapp.data.repository.PokemonRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * ViewModel para el FavoritesFragment (Pokédex).
 *
 * Responsabilidades:
 * - Exponer la lista de Pokémon capturados desde Room
 * - Permitir liberar (eliminar) Pokémon de la base de datos
 * - [MEJORA] Exponer cantidad de curas y Pokémon heridos
 */
class FavoritosViewModel(private val repository: PokemonRepository) : ViewModel() {

    private val TAG = "FavoritosViewModel"

    /**
     * Lista de Pokémon capturados, convertida de Flow a LiveData.
     */
    val listaPokemon: LiveData<List<PokemonEntity>> =
        repository.misPokemonCapturados.asLiveData()

    /**
     * Contador de Pokémon capturados.
     */
    val contadorCapturados: LiveData<Int> =
        repository.contadorCapturados.asLiveData()

    /**
     * [MEJORA] Cantidad de curas/pociones disponibles.
     */
    val cantidadCuras: LiveData<Int?> =
        repository.obtenerCantidadCuras()?.map { it ?: 0 }?.asLiveData()
            ?: androidx.lifecycle.MutableLiveData(0)

    /**
     * [MEJORA] Contador de Pokémon heridos.
     */
    val contadorHeridos: LiveData<Int> =
        repository.contarPokemonHeridos().asLiveData()

    init {
        // Inicializar inventario si no existe
        viewModelScope.launch {
            repository.inicializarInventario()
        }
    }

    /**
     * Libera (elimina) un Pokémon de la Pokédex.
     */
    fun liberarPokemon(pokemon: PokemonEntity) {
        Log.d(TAG, "🔓 Liberando ${pokemon.nombre}...")
        viewModelScope.launch {
            repository.liberarPokemon(pokemon)
            Log.d(TAG, "✅ ${pokemon.nombre} liberado correctamente")
        }
    }

    /**
     * Libera todos los Pokémon (limpia la Pokédex).
     */
    fun liberarTodos() {
        Log.d(TAG, "🔓 Liberando todos los Pokémon...")

        viewModelScope.launch {
            repository.liberarTodos()
            Log.d(TAG, "✅ Pokédex vaciada")
        }
    }
}

