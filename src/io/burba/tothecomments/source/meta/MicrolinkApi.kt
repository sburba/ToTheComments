package io.burba.tothecomments.source.meta

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

data class MicrolinkResponse(val data: MicrolinkMeta)
data class MicrolinkMeta(val title: String?, val image: MicrolinkImage?, val description: String?)
data class MicrolinkImage(val url: String?)

interface MicrolinkApi {
    @GET("/")
    fun getMeta(@Query("url") url: String): Call<MicrolinkResponse>
}