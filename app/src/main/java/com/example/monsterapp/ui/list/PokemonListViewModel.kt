package com.example.monsterapp.ui.list

import android.app.Application
import androidx.lifecycle.*
import com.example.monsterapp.MonsterApplication
import com.example.monsterapp.data.model.PokemonEntry
import kotlinx.coroutines.launch

class PokemonListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as MonsterApplication).repository

    private val _pokemonList = MutableLiveData<List<PokemonEntry>>()
    val pokemonList: LiveData<List<PokemonEntry>> = _pokemonList

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var currentOffset = 0
    private val pageSize = 20
    private var isLastPage = false
    private var isFetching = false

    private val loadedItems = mutableListOf<PokemonEntry>()

    init {
        loadNextPage()
    }

    fun loadNextPage() {
        if (isFetching || isLastPage) return
        isFetching = true
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val newItems = repository.getPokemonList(pageSize, currentOffset)
                loadedItems.addAll(newItems)
                _pokemonList.value = loadedItems.toList()
                currentOffset += pageSize
                if (newItems.size < pageSize) isLastPage = true
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading Pokémon"
            } finally {
                _isLoading.value = false
                isFetching = false
            }
        }
    }

    fun refresh() {
        currentOffset = 0
        isLastPage = false
        isFetching = false
        loadedItems.clear()
        _pokemonList.value = emptyList()
        loadNextPage()
    }
}
