package com.coroutines.playground.network

import com.coroutines.playground.network.models.StatusResponse
import retrofit2.http.GET

interface DigitalOceanStatusEndpoint {

    @GET("/api/v2/summary.json")
    suspend fun getSummary() : StatusResponse
}