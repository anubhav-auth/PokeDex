package com.example.pokedex.data

import com.example.pokedex.data.model.Pokemon
import com.example.pokedex.data.model.Result
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {
    suspend fun getPokemonList(limit: Int, offset: Int): Flow<Response<List<Result>>>
    suspend fun getPokemonDetail(name: String): Flow<Response<Pokemon>>
}