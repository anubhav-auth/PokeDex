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


    init {
        viewModelScope.launch {
            pokemonRepository.getPokemonList(20, 0).collectLatest { result ->

                when (result) {
                    is Response.Error -> _showErrorChannel.send(true)
                    is Response.Success -> {
                        result.data?.let { pokemon ->
                            Log.d("myTag", pokemon[0].url)
                            Log.d("myTag", pokemon[0].name)
                            _pokemon.update { pokemon }
                        }
                    }
                }

            }
        }
    }
}