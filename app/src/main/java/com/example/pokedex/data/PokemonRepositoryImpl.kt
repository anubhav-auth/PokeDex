package com.example.pokedex.data


import com.example.pokedex.data.model.Pokemon
import com.example.pokedex.data.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PokemonRepositoryImpl(
    private val api: Api
) : PokemonRepository {
    override suspend fun getPokemonList(limit: Int, offset: Int): Flow<Response<List<Result>>> {
        return flow {
            val pokemonFromApi = try {
                api.getPokemonList(limit, offset)
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Response.Error(message = "Problem Loading"))
                return@flow
            }
            emit(Response.Success(pokemonFromApi.results))
        }
    }

    override suspend fun getPokemonDetail(name: String): Flow<Response<Pokemon>> {
        return flow {
            val pokemonDetailFromApi = try {
                api.getPokemonDetail(name)
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Response.Error(message = "Problem Loading"))
                return@flow
            }
            emit(Response.Success(pokemonDetailFromApi))
        }
    }

}