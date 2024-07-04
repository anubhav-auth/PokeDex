package com.example.pokedex.presentation

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.example.pokedex.data.Cache
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

    private var allPokemonAlreadyLoaded: List<Result> = emptyList()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    fun loadAlreadyLoadedList() {
        _pokemon.value = allPokemonAlreadyLoaded
    }

    init {
        loadPokemon()
    }

    fun loadPokemon() {
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
                                _pokemon.update { currentList ->
                                    currentList + newPokemon
                                }
                                allPokemonAlreadyLoaded += newPokemon
                                currentOffset += limit
                            }
                            isLoading = false
                        }
                    }

                }
            }
        }
    }

    fun searchPokemon(query: String) {
        _searchQuery.value = query.trim()
        val query2 = query.trim()
        _pokemon.value = Cache.fullPokemonList.filter { pokemon ->
            pokemon.name.contains(query2, ignoreCase = true)
        }
    }

//    fun calcDominantColor(drawable: Drawable, onFinish: (Color) -> Unit) {
//        val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)
//
//        Palette.from(bmp).generate { palette ->
//            palette?.dominantSwatch?.rgb?.let { colorValue ->
//                onFinish(Color(colorValue))
//            }
//        }
//    }
}