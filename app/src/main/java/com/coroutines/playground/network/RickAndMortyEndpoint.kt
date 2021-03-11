package com.coroutines.playground.network

import com.coroutines.playground.network.models.CharacterResp
import retrofit2.http.GET

interface RickAndMortyEndpoint {

    @GET("/api/character/")
    suspend fun getAllCharacters() : CharacterResp
}