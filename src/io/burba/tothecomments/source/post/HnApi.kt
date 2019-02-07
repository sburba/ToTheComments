package io.burba.tothecomments.source.post

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

data class HnResponse(val hits: List<Hit>)
data class Hit(val title: String, val objectID: String)

interface HnApi {
    @GET("api/v1/search?restrictSearchableAttributes=url")
    fun getPosts(@Query("query") url: String): Call<HnResponse>
}