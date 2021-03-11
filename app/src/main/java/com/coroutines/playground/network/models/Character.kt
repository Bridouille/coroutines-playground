package com.coroutines.playground.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CharacterResp(
    @Json(name = "info")
    val info: RespInfo,

    @Json(name = "results")
    val results: List<Character>
)

@JsonClass(generateAdapter = true)
data class RespInfo(
    @Json(name = "count")
    val count: Int,

    @Json(name = "pages")
    val pages: Int,
)

@JsonClass(generateAdapter = true)
data class Character(
    @Json(name = "id")
    val id: Long,

    @Json(name = "name")
    val name: String,

    @Json(name = "status")
    val status: String,

    @Json(name = "species")
    val species: String,

    @Json(name = "image")
    val image: String
)