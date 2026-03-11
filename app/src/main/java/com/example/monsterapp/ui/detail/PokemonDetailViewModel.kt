package com.example.monsterapp.ui.detail

import android.app.Application
import androidx.lifecycle.*
import com.example.monsterapp.MonsterApplication
import com.example.monsterapp.data.db.FavoritePokemon
import com.example.monsterapp.data.model.PokemonDetail
import kotlinx.coroutines.launch
import java.util.Locale

class PokemonDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as MonsterApplication).repository

    private val _pokemonDetail = MutableLiveData<PokemonDetail?>()
    val pokemonDetail: LiveData<PokemonDetail?> = _pokemonDetail

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _isFavorite = MutableLiveData(false)
    val isFavorite: LiveData<Boolean> = _isFavorite

    fun loadPokemon(id: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val detail = repository.getPokemonDetail(id)
                _pokemonDetail.value = detail
                _isFavorite.value = repository.isFavorite(id)
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading Pokémon details"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite() {
        val detail = _pokemonDetail.value ?: return
        viewModelScope.launch {
            val isFav = repository.isFavorite(detail.id)
            if (isFav) {
                val favorite = repository.getFavoriteById(detail.id) ?: return@launch
                repository.removeFavorite(favorite)
                _isFavorite.value = false
            } else {
                val imageUrl = detail.sprites.other?.officialArtwork?.frontDefault
                    ?: detail.sprites.frontDefault
                    ?: ""
                val typesStr = detail.types.joinToString(", ") { it.type.name }
                repository.addFavorite(
                    FavoritePokemon(
                        id = detail.id,
                        name = detail.name.replaceFirstChar { it.titlecase(Locale.ROOT) },
                        imageUrl = imageUrl,
                        types = typesStr,
                        height = detail.height,
                        weight = detail.weight
                    )
                )
                _isFavorite.value = true
            }
        }
    }
}
