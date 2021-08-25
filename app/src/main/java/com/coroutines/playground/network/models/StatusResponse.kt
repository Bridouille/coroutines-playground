package com.coroutines.playground.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StatusResponse(
    @Json(name = "page")
    val page: PageInfo,

    @Json(name = "components")
    val components: List<ComponentInfo>,

    @Json(name = "status")
    val status: SummaryStatus
)

@JsonClass(generateAdapter = true)
data class PageInfo(
    @Json(name = "id")
    val id: String,

    @Json(name = "name")
    val name: String,

    @Json(name = "url")
    val url: String,

    @Json(name = "time_zone")
    val timeZone: String,

    @Json(name = "updated_at")
    val updatedAt: String,
)

@JsonClass(generateAdapter = true)
data class ComponentInfo(
    @Json(name = "id")
    val id: String,

    @Json(name = "name")
    val name: String,

    @Json(name = "status")
    val status: String,
)

@JsonClass(generateAdapter = true)
data class SummaryStatus(
    @Json(name = "indicator")
    val indicator: String,

    @Json(name = "description")
    val description: String,
)