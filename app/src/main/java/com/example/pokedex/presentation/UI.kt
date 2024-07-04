package com.example.pokedex.presentation

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.pokedex.R
import com.example.pokedex.data.model.Result
import com.example.pokedex.data.model.Type
import com.example.pokedex.ui.theme.backGrad1
import com.example.pokedex.ui.theme.backGrad2
import com.example.pokedex.ui.theme.pokemonListItemBack
import kotlinx.coroutines.flow.collectLatest

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(backGrad2, backGrad1, backGrad2),
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
//            Spacer(modifier = Modifier.size(36.dp))
            Image(
                painter = painterResource(id = R.drawable.poke_homepage_logo),
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
    navController: NavController,
    dominantColor: Color
) {

    viewmodel.fetchPokemonDetail(pokemonName)
    val pokemonDetail by viewmodel.pokemonDetail.collectAsState()
    val interactionSource = remember {
        MutableInteractionSource()
    }
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
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(backGrad2, backGrad1, backGrad1, backGrad2),
                        start = Offset.Zero,
                        end = Offset.Infinite
                    )
                )
        ) {

            item {
                Spacer(modifier = Modifier.height(18.dp))
                Box(
                    modifier = Modifier
                        .fillMaxSize(), contentAlignment = Alignment.TopStart
                ) {

                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "backArrow",
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .size(30.dp)
                            .clickable(interactionSource = interactionSource, indication = null) {
                                navController.navigateUp()
                            }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "#${detail.id}",
                            fontSize = 30.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.size(48.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = detail.sprites.front_default,
                        contentDescription = detail.name,
                        imageLoader = imageLoader,
                        modifier = Modifier
                            .size(350.dp)
                            .background(
                                Brush.radialGradient(
                                    listOf(dominantColor, Color.Transparent),
                                    center = Offset.Unspecified,
                                    radius = Float.POSITIVE_INFINITY,
                                    tileMode = TileMode.Clamp
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(50.dp),
                        alignment = Alignment.Center,
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(25.dp))
                    Text(
                        text = detail.name[0].uppercase() + detail.name.substring(1),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(25.dp))
                    DynamicChip(chipText = detail.types, border = dominantColor)
                    Spacer(modifier = Modifier.size(25.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.height),
                                contentDescription = "height icon",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(15.dp))
                            Text(
                                " ${detail.height}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.weight),
                                contentDescription = "weight logo",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(15.dp))
                            Text(
                                " ${detail.weight}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.size(25.dp))
                    StatProgressbar(
                        label = "BASE Ex",
                        value = detail.base_experience,
                        maxValue = 635,
                        barColor = dominantColor
                    )
                    StatProgressbar(
                        label = "HP",
                        value = detail.stats[0].base_stat,
                        maxValue = 255,
                        barColor = dominantColor
                    )
                    StatProgressbar(
                        label = "ATK",
                        value = detail.stats[1].base_stat,
                        maxValue = 190,
                        barColor = dominantColor
                    )
                    StatProgressbar(
                        label = "DEF",
                        value = detail.stats[2].base_stat,
                        maxValue = 250,
                        barColor = dominantColor
                    )
                    StatProgressbar(
                        label = "SPD",
                        value = detail.stats[5].base_stat,
                        maxValue = 200,
                        barColor = dominantColor
                    )
                    StatProgressbar(
                        label = "Sp. ATK",
                        value = detail.stats[3].base_stat,
                        maxValue = 194,
                        barColor = dominantColor
                    )
                    StatProgressbar(
                        label = "Sp. DEF",
                        value = detail.stats[4].base_stat,
                        maxValue = 250,
                        barColor = dominantColor
                    )
                }
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
            .shadow(15.dp)
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
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(6.dp))
    }
}

@Composable
fun StatProgressbar(label: String, value: Int, maxValue: Int, barColor: Color = Color.White) {
    val progress = remember {
        mutableFloatStateOf(0f)
    }
    val animatedProgress = animateFloatAsState(targetValue = progress.floatValue, label = "")

    LaunchedEffect(key1 = value) {
        progress.floatValue = value / maxValue.toFloat()
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 12.dp, horizontal = 15.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.width(75.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value.toString(),
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.width(40.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .height(20.dp)
                .fillMaxWidth(0.95f)
                .background(Color(0xFF121212), RoundedCornerShape(10.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress.value)
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(
                                barColor,
                                barColor,
                                barColor,
                                Color.White
                            )
                        ), RoundedCornerShape(10.dp)
                    )
            )
        }
    }
}

fun getPokemonImageUrl(id: Int): String {
    return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DynamicChip(chipText: List<Type>, border: Color) {
    FlowRow(
        modifier = Modifier
            .padding(8.dp)
            .wrapContentSize()
    ) {
        chipText.forEach { type ->
            Chip(type.type.name, border)
        }
    }
}

@Composable
fun Chip(text: String = "default", border: Color) {
    Box(
        modifier = Modifier
            .padding(start = 6.dp, top = 9.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, border, RoundedCornerShape(18.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold
        )
    }
}