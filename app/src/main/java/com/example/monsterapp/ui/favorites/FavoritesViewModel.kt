package com.example.monsterapp.ui.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.monsterapp.MonsterApplication
import com.example.monsterapp.data.db.FavoritePokemon
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as MonsterApplication).repository

    val favorites: LiveData<List<FavoritePokemon>> = repository.allFavorites

    fun removeFavorite(pokemon: FavoritePokemon) {
        viewModelScope.launch {
            repository.removeFavorite(pokemon)
        }
    }
}
