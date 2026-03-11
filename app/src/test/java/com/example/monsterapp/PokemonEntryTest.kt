package com.example.monsterapp

import com.example.monsterapp.data.model.PokemonEntry
import org.junit.Assert.*
import org.junit.Test

class PokemonEntryTest {

    @Test
    fun `pokemonEntry id is parsed from url correctly`() {
        val entry = PokemonEntry(
            name = "bulbasaur",
            url = "https://pokeapi.co/api/v2/pokemon/1/"
        )
        assertEquals(1, entry.id)
    }

    @Test
    fun `pokemonEntry imageUrl is constructed from id`() {
        val entry = PokemonEntry(
            name = "charmander",
            url = "https://pokeapi.co/api/v2/pokemon/4/"
        )
        val expectedUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/4.png"
        assertEquals(expectedUrl, entry.imageUrl)
    }

    @Test
    fun `pokemonEntry id returns 0 for malformed url`() {
        val entry = PokemonEntry(
            name = "unknown",
            url = "https://pokeapi.co/api/v2/pokemon/abc/"
        )
        assertEquals(0, entry.id)
    }
}
