package com.xbadut.pokedex.pokemonlist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.ImagePainter
import com.xbadut.pokedex.R
import com.xbadut.pokedex.data.model.PokedexListEntry
import com.xbadut.pokedex.ui.theme.PokedexTheme
import com.xbadut.pokedex.ui.theme.RobotoCondensed
import coil.compose.rememberImagePainter
import coil.request.ImageRequest


@Preview(showBackground = true)
@ExperimentalFoundationApi

@Composable
fun PreviewPokemonListPage() {
    PokedexTheme {
        val navController = rememberNavController()

        PokemonListPage(navController)
    }
}

@ExperimentalFoundationApi
@Composable
fun PokemonListPage(
    navController: NavController,
    viewModel: PokemonListViewModel = hiltViewModel()
) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize(),
    ) {
        Column {
            Spacer(
                modifier = Modifier.padding(top = 16.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.ic_international_pok_mon_logo),
                contentDescription = "Pokemon Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(CenterHorizontally)

            )
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                hint = "Search..."
            ) {
                viewModel.searchPokemonList(it)
            }

            Spacer(modifier = Modifier.height(16.dp))
            PokemonList(
                navController = navController
            )

        }
    }
}

@Composable
fun SearchBar(
    modifier: Modifier,
    hint: String = "",
    onSearch: (String) -> Unit = {}
) {
    var text by remember {
        mutableStateOf("")
    }

    var isHintDisplayed by remember {
        mutableStateOf(hint != "")
    }

    Box(
        modifier = modifier,
    ) {
        BasicTextField(
            value = text,
            onValueChange = {
                text = it
                onSearch(it)
            },
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp, CircleShape)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .onFocusChanged {
                    isHintDisplayed = it.isFocused
                }
        )

        if (!isHintDisplayed) {
            Text(
                text = hint,
                color = Color.LightGray,
                modifier = Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 12.dp
                )
            )
        }

    }

}

@ExperimentalFoundationApi
@Composable
fun PokemonList(
   navController: NavController,
   viewModel: PokemonListViewModel = hiltViewModel()
) {
    val pokemonList by remember {
        viewModel.pokemonList
    }
    
    val endReached by remember {
        viewModel.endReached
    }
    
    val loadError by remember {
        viewModel.loadError
    }
    
    val isLoading by remember {
        viewModel.isLoading
    }

    LazyVerticalGrid(
        cells = GridCells.Fixed(2),
    ) {
        items(
            count = pokemonList.size,
        ) {

            if(it >= pokemonList.size - 1 && !endReached && !isLoading) viewModel.loadPokemonPaginated()
            PokedexEntry(
                entry = pokemonList[it],
                navController = navController,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        }
    }
    
    Box(
        contentAlignment = Center,
        modifier = Modifier.fillMaxSize()
    ) {
        
        if(isLoading) CircularProgressIndicator(color = MaterialTheme.colors.primary)
        
        if(loadError.isNotEmpty())  Text(text = viewModel.loadError.value)
    }

}

@Composable
fun PokedexEntry(
    entry: PokedexListEntry,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val defaultDominantColor = MaterialTheme.colors.surface
    Box(
        modifier = modifier
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .aspectRatio(1f)
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        defaultDominantColor,
                        defaultDominantColor
                    )
                )
            )
            .clickable {
                navController.navigate(
                    "pokemon_detail_screen/${entry.pokemonName}"
                )
            }
    ) {
        Column {

            val painter = rememberImagePainter(
                request = ImageRequest
                    .Builder(LocalContext.current)
                    .data(entry.imageUrl)
                    .build(),
            )


            Box(
                modifier = Modifier
                    .size(120.dp)
                    .align(CenterHorizontally)
            ) {
                Image(
                    painter = painter,
                    contentDescription = entry.pokemonName,
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Center)
                )

                if (painter.state is ImagePainter.State.Loading) CircularProgressIndicator(
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.align(Center)
                )
            }

            Text(
                text = entry.pokemonName,
                fontFamily = RobotoCondensed,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

