package com.example.monsterapp.data.remote

import com.example.monsterapp.data.model.PokemonDetail
import com.example.monsterapp.data.model.PokemonListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interfaz que define los endpoints de PokeAPI.
 * Retrofit genera automáticamente la implementación.
 */
interface PokeApiService {

    /**
     * Obtiene una lista de Pokémon de la API.
     * Endpoint: https://pokeapi.co/api/v2/pokemon?limit=X
     *
     * @param limit Número máximo de Pokémon a obtener
     * @return Respuesta con la lista de Pokémon
     */
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int
    ): PokemonListResponse

    /**
     * Obtiene los detalles completos de un Pokémon específico.
     * Endpoint: https://pokeapi.co/api/v2/pokemon/{id}
     *
     * @param id ID o nombre del Pokémon
     * @return Detalles completos del Pokémon
     */
    @GET("pokemon/{id}")
    suspend fun getPokemonDetail(
        @Path("id") id: Int
    ): PokemonDetail
}

