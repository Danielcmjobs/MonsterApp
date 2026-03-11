package com.example.monsterapp.data.repository

import androidx.lifecycle.LiveData
import com.example.monsterapp.data.api.NetworkModule
import com.example.monsterapp.data.db.FavoritePokemon
import com.example.monsterapp.data.db.FavoritePokemonDao
import com.example.monsterapp.data.model.PokemonDetail
import com.example.monsterapp.data.model.PokemonEntry

class PokemonRepository(private val favoritePokemonDao: FavoritePokemonDao) {

    private val apiService = NetworkModule.pokeApiService

    // --- API calls ---

    suspend fun getPokemonList(limit: Int, offset: Int): List<PokemonEntry> {
        return apiService.getPokemonList(limit, offset).results
    }

    suspend fun getPokemonDetail(id: Int): PokemonDetail {
        return apiService.getPokemonDetail(id)
    }

    // --- Favorites (Room) ---

    val allFavorites: LiveData<List<FavoritePokemon>> = favoritePokemonDao.getAllFavorites()

    suspend fun addFavorite(pokemon: FavoritePokemon) {
        favoritePokemonDao.insertFavorite(pokemon)
    }

    suspend fun removeFavorite(pokemon: FavoritePokemon) {
        favoritePokemonDao.deleteFavorite(pokemon)
    }

    suspend fun isFavorite(pokemonId: Int): Boolean {
        return favoritePokemonDao.isFavorite(pokemonId) > 0
    }

    suspend fun getFavoriteById(pokemonId: Int): FavoritePokemon? {
        return favoritePokemonDao.getFavoriteById(pokemonId)
    }
}
