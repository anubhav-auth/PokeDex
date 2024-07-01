package com.example.pokedex

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.pokedex.data.PokemonRepositoryImpl
import com.example.pokedex.data.RetrofitInstance
import com.example.pokedex.data.model.Result
import com.example.pokedex.presentation.PokemonDetailViewModel
import com.example.pokedex.presentation.PokemonViewModel
import com.example.pokedex.ui.theme.PokeDexTheme
import kotlinx.coroutines.flow.collectLatest

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

                NavHost(navController = navController, startDestination = "pokemon_listScreen") {
                    composable("pokemon_listScreen") {
                        homeScreen(viewmodel = viewmodel, navController = navController)
                    }
                    composable("Pokemon_detailScreen/{name}") {
                        val pokemonName = it.arguments?.getString("name") ?: "ditto"
                        detailScreen(
                            pokemonName = pokemonName,
                            viewmodel = detailViewmodel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun homeScreen(viewmodel: PokemonViewModel, navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        val pokemonList = viewmodel.pokemon.collectAsState().value
        val context = LocalContext.current
        LaunchedEffect(key1 = viewmodel.showErrorChannel) {
            viewmodel.showErrorChannel.collectLatest { show ->
                if (show) {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
        if ((pokemonList.isEmpty())) {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                contentPadding = PaddingValues(16.dp)
            ) {
                items(pokemonList.size) { index ->
                    Pokemon(pokemonList[index], navController = navController)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun detailScreen(
    pokemonName: String, viewmodel: PokemonDetailViewModel
) {
    viewmodel.fetchPokemonDetail(pokemonName)
    val pokemonDetail by viewmodel.pokemonDetail.collectAsState()

    val context = LocalContext.current
    LaunchedEffect(key1 = viewmodel.showErrorChannel) {
        viewmodel.showErrorChannel.collectLatest { show ->
            if (show) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }
    val imageLoader = ImageLoader.Builder(context).crossfade(true).build()

    pokemonDetail?.let { detail ->
        LazyColumn(modifier = Modifier.padding(16.dp)) {

            item {
                Text(detail.name, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = detail.sprites.front_default,
                    contentDescription = detail.name,
                    imageLoader = imageLoader,
                    modifier = Modifier.size(128.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Height: ${detail.height}")
                Text("Weight: ${detail.weight}")
                Text("Base Experience: ${detail.base_experience}")
                Spacer(modifier = Modifier.height(16.dp))
                Text("Stats", style = MaterialTheme.typography.bodySmall)
            }
            items(detail.stats) { stat ->
                Text("${stat.stat.name}: ${stat.base_stat}")
            }
        }
    }

}

@Composable
fun Pokemon(pokemon: Result, navController: NavController) {

    val imageState = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current).data(
            getPokemonImageUrl(pokemon.url)
        ).size(Size.ORIGINAL).build()
    ).state

    Column(modifier = Modifier
        .clip(RoundedCornerShape(20.dp))
        .height(300.dp)
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.primaryContainer)
        .clickable {
            navController.navigate("Pokemon_detailScreen/${pokemon.name}")
        }
    ) {

        if (imageState is AsyncImagePainter.State.Error) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        if (imageState is AsyncImagePainter.State.Success) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                painter = imageState.painter,
                contentDescription = pokemon.name,
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = pokemon.name,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(6.dp))
    }
}

fun getPokemonImageUrl(pokemonUrl: String): String {
    val id = pokemonUrl.split("/").last { it.isNotEmpty() }
    return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
}