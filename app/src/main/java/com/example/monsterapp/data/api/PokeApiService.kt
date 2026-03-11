package com.example.monsterapp.data.api

import com.example.monsterapp.data.model.PokemonDetail
import com.example.monsterapp.data.model.PokemonListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApiService {

    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): PokemonListResponse

    @GET("pokemon/{id}")
    suspend fun getPokemonDetail(
        @Path("id") id: Int
    ): PokemonDetail

    @GET("pokemon/{name}")
    suspend fun getPokemonDetailByName(
        @Path("name") name: String
    ): PokemonDetail
}
