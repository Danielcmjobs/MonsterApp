package com.example.monsterapp

import android.app.Application
import com.example.monsterapp.data.db.AppDatabase
import com.example.monsterapp.data.repository.PokemonRepository

class MonsterApplication : Application() {

    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val repository: PokemonRepository by lazy { PokemonRepository(database.favoritePokemonDao()) }
}
