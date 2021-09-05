package com.xbadut.pokedex.di

import com.xbadut.pokedex.data.remote.PokemonApi
import com.xbadut.pokedex.repository.PokemonRepository
import com.xbadut.pokedex.util.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providePokemonRepository(
        api: PokemonApi
    ) = PokemonRepository(api)

    @Singleton
    @Provides
    fun providePokeApi() : PokemonApi = Retrofit
        .Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()
        .create(PokemonApi::class.java)
}