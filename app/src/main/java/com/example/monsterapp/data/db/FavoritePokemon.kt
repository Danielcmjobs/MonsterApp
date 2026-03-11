package com.example.monsterapp.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_pokemon")
data class FavoritePokemon(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String,
    val types: String,
    val height: Int,
    val weight: Int,
    val addedAt: Long = System.currentTimeMillis()
)
