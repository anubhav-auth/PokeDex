package com.example.pokedex.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.PokemonRepository
import com.example.pokedex.data.Response
import com.example.pokedex.data.model.Pokemon
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PokemonDetailViewModel(
    private val pokemonRepository: PokemonRepository
) : ViewModel() {

    private val _pokemonDetail = MutableStateFlow<Pokemon?>(null)
    val pokemonDetail = _pokemonDetail.asStateFlow()

    private val _showErrorChannel = Channel<Boolean>()
    val showErrorChannel = _showErrorChannel.receiveAsFlow()

    fun fetchPokemonDetail(name: String) {
        viewModelScope.launch {
            pokemonRepository.getPokemonDetail(name).collectLatest { result ->
                when (result) {
                    is Response.Error -> _showErrorChannel.send(true)
                    is Response.Success -> {

                        result.data?.let { details ->

                            _pokemonDetail.update {
                                details
                            }

                        }
                    }
                }
            }
        }
    }

}