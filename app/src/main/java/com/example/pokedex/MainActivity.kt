package com.example.pokedex

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
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
import com.example.pokedex.ui.theme.backGrad1
import com.example.pokedex.ui.theme.backGrad2
import com.example.pokedex.ui.theme.pokemonListItemBack
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
                val imageLoader = ImageLoader.Builder(this).crossfade(true).build()

                NavHost(navController = navController, startDestination = "pokemon_listScreen") {
                    composable("pokemon_listScreen") {
                        HomeScreen(
                            viewmodel = viewmodel,
                            navController = navController
                        )
                    }
                    composable("Pokemon_detailScreen/{name}/{dominantColor}") {
                        val pokemonName = it.arguments?.getString("name") ?: "ditto"
                        val dominantColor = it.arguments?.getString("dominantColor")?.toIntOrNull()
                            ?.let { Color(it) } ?: Color.Blue
                        DetailScreen(
                            pokemonName = pokemonName,
                            viewmodel = detailViewmodel,
                            imageLoader = imageLoader,
                            dominantColor = dominantColor

                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Searchbar(
    viewmodel: PokemonViewModel,
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit = {}
) {
    var txt by remember {
        mutableStateOf("")
    }
    LaunchedEffect(key1 = viewmodel.searchQuery) {
        txt = viewmodel.searchQuery.value
    }
    BasicTextField(
        value = txt,
        onValueChange = {
            txt = it
            onSearch(it)
        },
        maxLines = 1,
        singleLine = true,
        textStyle = TextStyle(fontSize = 27.sp),
        modifier = modifier
            .padding(vertical = 21.dp, horizontal = 21.dp)
            .clip(RoundedCornerShape(15.dp))
            .fillMaxWidth()
            .height(45.dp)
            .background(Color.White)
            .border(
                3.dp,
                Brush.linearGradient(
                    listOf(
                        Color(0xFFFEC20C),
                        Color(0xFF2D4596),
                        Color(0xFFFEC20C),
                        Color(0xFF2D4596)
                    )
                ),
                RoundedCornerShape(15.dp)
            )
            .padding(horizontal = 9.dp, vertical = 5.dp)

    )
}

@Composable
fun HomeScreen(
    viewmodel: PokemonViewModel,
    navController: NavController
) {

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(backGrad1, backGrad1, backGrad2),
                    start = Offset.Zero,
                    end = Offset.Infinite
                )
            )
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
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.size(36.dp))
            Image(
                painter = painterResource(id = R.drawable.poke_logo),
                contentDescription = "poke Logo"
            )
            Searchbar(viewmodel) {
                if (it.isNotBlank()) {
                    viewmodel.searchPokemon(it)
                } else {
                    viewmodel.loadAlreadyLoadedList()
                }
            }
            when {
                pokemonList.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2)
                    ) {
                        itemsIndexed(pokemonList) { index, items ->
                            Pokemon(pokemon = items, navController = navController)
//                          one method to load more pokemons
                            if (index >= pokemonList.size - 1) {
                                viewmodel.loadPokemon()
                            }
                        }
                    }
                }
            }
        }


    }
}

@Composable
fun DetailScreen(
    pokemonName: String,
    viewmodel: PokemonDetailViewModel,
    imageLoader: ImageLoader,
    dominantColor: Color
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


    pokemonDetail?.let { detail ->
        LazyColumn(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(backGrad1, backGrad1, backGrad2),
                        start = Offset.Zero,
                        end = Offset.Infinite
                    )
                )
        ) {

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
    val id = pokemon.url.split("/").last { it.isNotEmpty() }.toInt()

    val imageState = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current).data(
            getPokemonImageUrl(id)
        ).size(Size.ORIGINAL).build()
    ).state

    val dominantColor = Color.Green.toArgb()

    Column(
        modifier = Modifier
            .padding(12.dp)
            .clip(RoundedCornerShape(20.dp))
            .size(250.dp)
            .background(pokemonListItemBack)
            .clickable {
                navController.navigate("Pokemon_detailScreen/${pokemon.name}/${dominantColor}")
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Text(text = "#$id", color = Color(0xFF76BBD7))
        if (imageState is AsyncImagePainter.State.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        if (imageState is AsyncImagePainter.State.Success) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                painter = imageState.painter,
                contentDescription = pokemon.name,
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = pokemon.name[0].uppercase() + pokemon.name.substring(1),
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(6.dp))
    }
}

fun getPokemonImageUrl(id: Int): String {
    return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
}