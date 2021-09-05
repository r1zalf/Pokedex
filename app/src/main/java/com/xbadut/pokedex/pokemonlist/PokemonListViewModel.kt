package com.xbadut.pokedex.pokemonlist

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xbadut.pokedex.data.model.PokedexListEntry
import com.xbadut.pokedex.repository.PokemonRepository
import com.xbadut.pokedex.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private var currentPage = 0

    val pokemonList = mutableStateOf<List<PokedexListEntry>>(listOf())
    private var tempPokemonList = listOf<PokedexListEntry>()

    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)

    init {
        loadPokemonPaginated()
    }

    fun searchPokemonList(query: String) {
        viewModelScope.launch(Dispatchers.Default) {
            if (query.isEmpty()) {
                pokemonList.value = tempPokemonList
                return@launch
            }
            pokemonList.value = tempPokemonList.filter {
                it.pokemonName.contains(query.trim(), ignoreCase = true) ||
                        it.number.toString() == query.trim()
            }
        }
    }

    fun loadPokemonPaginated() {
        viewModelScope.launch {
            isLoading.value = true
            when(val result = repository.getPokemonList(20, currentPage * 20)) {
                is Resource.Success -> {
                    endReached.value = currentPage * 20 >= result.data!!.count
                    val pokedexEntries = result.data.results.mapIndexed { _, entry ->
                        val number = if(entry.url.endsWith("/")) {
                            entry.url.dropLast(1).takeLastWhile { it.isDigit() }
                        } else {
                            entry.url.takeLastWhile { it.isDigit() }
                        }
                        val url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"

                        PokedexListEntry(
                            entry.name.uppercase(Locale.getDefault()),
                            url,
                            number.toInt()
                            )
                    }
                    currentPage++

                    loadError.value = ""
                    isLoading.value = false
                    pokemonList.value += pokedexEntries
                    tempPokemonList = tempPokemonList + pokedexEntries
                }

                is Resource.Error -> {
                    loadError.value = "Ada yang salah"
                    isLoading.value = false
                }
            }
        }
    }

}