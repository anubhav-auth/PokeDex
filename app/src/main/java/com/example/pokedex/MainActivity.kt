package com.example.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import com.example.pokedex.data.PokemonRepositoryImpl
import com.example.pokedex.data.RetrofitInstance
import com.example.pokedex.presentation.DetailScreen
import com.example.pokedex.presentation.HomeScreen
import com.example.pokedex.presentation.PokemonDetailViewModel
import com.example.pokedex.presentation.PokemonViewModel
import com.example.pokedex.ui.theme.PokeDexTheme


class MainActivity : ComponentActivity() {

    private val viewmodel by viewModels<PokemonViewModel>(factoryProducer = {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PokemonViewModel(PokemonRepositoryImpl(RetrofitInstance.api)) as T
            }
        }
    })

    private val detailViewmodel by viewModels<PokemonDetailViewModel>(factoryProducer = {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PokemonDetailViewModel(PokemonRepositoryImpl(RetrofitInstance.api)) as T
            }
        }
    })


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokeDexTheme {
                val navController = rememberNavController()
                val imageLoader = ImageLoader.Builder(this).crossfade(true).build()
                Scaffold { padd ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues = padd)
                    ) {

                        NavHost(
                            navController = navController, startDestination = "pokemon_listScreen"
                        ) {
                            composable("pokemon_listScreen") {
                                HomeScreen(
                                    viewmodel = viewmodel, navController = navController
                                )
                            }
                            composable("Pokemon_detailScreen/{name}/{dominantColor}") {
                                val pokemonName = it.arguments?.getString("name") ?: "ditto"
                                val dominantColor =
                                    it.arguments?.getString("dominantColor")?.toIntOrNull()
                                        ?.let { Color(it) } ?: Color.Blue
                                DetailScreen(
                                    pokemonName = pokemonName,
                                    viewmodel = detailViewmodel,
                                    imageLoader = imageLoader,
                                    navController = navController,
                                    dominantColor = dominantColor
                                )
                            }
                        }
                    }
                }

            }
        }
    }
}

