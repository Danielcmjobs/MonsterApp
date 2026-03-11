package com.example.monsterapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.monsterapp.data.repository.PokemonRepository

/**
 * Factory para crear ViewModels que requieren dependencias en el constructor.
 *
 * Por defecto, Android solo puede crear ViewModels con constructor vacío.
 * Como nuestros ViewModels ahora necesitan el repositorio, usamos esta Factory.
 *
 * Uso en un Fragment:
 * ```
 * private val viewModel: MapViewModel by viewModels {
 *     val app = requireActivity().application as MonsterExplorerApp
 *     PokemonViewModelFactory(app.repository)
 * }
 * ```
 *
 * @param repository Repositorio que se inyectará en los ViewModels
 */
class PokemonViewModelFactory(
    private val repository: PokemonRepository
) : ViewModelProvider.Factory {

    /**
     * Crea la instancia del ViewModel solicitado.
     *
     * @param modelClass Clase del ViewModel a crear
     * @return Instancia del ViewModel
     * @throws IllegalArgumentException Si el ViewModel no está soportado
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Verificar qué tipo de ViewModel se está solicitando

        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(repository) as T
        }

        if (modelClass.isAssignableFrom(FavoritosViewModel::class.java)) {
            return FavoritosViewModel(repository) as T
        }

        if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
            return DetailsViewModel(repository) as T
        }

        if (modelClass.isAssignableFrom(BattleViewModel::class.java)) {
            return BattleViewModel(repository) as T
        }

        // Si llegamos aquí, el ViewModel no está soportado
        throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}

