package com.example.monsterapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FavoritePokemonDao {

    @Query("SELECT * FROM favorite_pokemon ORDER BY addedAt DESC")
    fun getAllFavorites(): LiveData<List<FavoritePokemon>>

    @Query("SELECT * FROM favorite_pokemon WHERE id = :pokemonId")
    suspend fun getFavoriteById(pokemonId: Int): FavoritePokemon?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(pokemon: FavoritePokemon)

    @Delete
    suspend fun deleteFavorite(pokemon: FavoritePokemon)

    @Query("SELECT COUNT(*) FROM favorite_pokemon WHERE id = :pokemonId")
    suspend fun isFavorite(pokemonId: Int): Int
}
