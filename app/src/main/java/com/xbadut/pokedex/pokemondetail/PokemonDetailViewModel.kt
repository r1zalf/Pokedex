package com.xbadut.pokedex.pokemondetail

import androidx.lifecycle.ViewModel
import com.xbadut.pokedex.data.remote.response.Pokemon
import com.xbadut.pokedex.repository.PokemonRepository
import com.xbadut.pokedex.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    suspend fun getPokemonInfo(pokemonName: String) : Resource<Pokemon> {
        return  repository.getPokemon(pokemonName)
    }
}