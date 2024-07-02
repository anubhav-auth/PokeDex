package com.example.pokedex.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.PokemonRepository
import com.example.pokedex.data.Response
import com.example.pokedex.data.model.Result
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PokemonViewModel(
    private val pokemonRepository: PokemonRepository
) : ViewModel() {
    private val _pokemon = MutableStateFlow<List<Result>>(emptyList())
    val pokemon = _pokemon.asStateFlow()

    private val _showErrorChannel = Channel<Boolean>()
    val showErrorChannel = _showErrorChannel.receiveAsFlow()

    private var currentOffset = 0
    private val limit = 20
    private var isLoading = false


    init {
        loadPokemon()
    }

    fun loadPokemon(){
        if (!isLoading) {
            isLoading = true
            viewModelScope.launch {
                pokemonRepository.getPokemonList(limit, currentOffset).collectLatest { result ->

                    when (result) {
                        is Response.Error -> {
                            _showErrorChannel.send(true)
                            isLoading = false
                        }
                        is Response.Success -> {
                            result.data?.let { newPokemon ->
                                _pokemon.update { currentList->
                                    currentList + newPokemon
                                }
                                currentOffset += limit
                            }
                            isLoading = false
                        }
                    }

                }
            }
        }
    }
}