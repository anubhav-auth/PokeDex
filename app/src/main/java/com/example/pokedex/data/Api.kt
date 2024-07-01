package com.example.pokedex.data

import com.example.pokedex.data.model.Pokemon
import com.example.pokedex.data.model.PokemonList
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url


interface Api{

    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit")limit:Int,
        @Query("offset")offset: Int
    ): PokemonList

    @GET("pokemon/{name}")
    suspend fun getPokemonDetail(
        @Path("name")name:String
    ): Pokemon

    companion object{
        const val BASE_URL = "https://pokeapi.co/api/v2/"
    }
}