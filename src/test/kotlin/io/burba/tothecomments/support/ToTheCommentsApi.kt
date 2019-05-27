package io.burba.tothecomments.support

import io.burba.tothecomments.action.UrlDetails
import io.burba.tothecomments.http.Success
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ToTheCommentsApi {
    @GET("/posts/{name}")
    fun getMeta(@Path("name") url: String): Call<Success<UrlDetails>>
}