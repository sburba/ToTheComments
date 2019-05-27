package io.burba.tothecomments.fake

import io.burba.tothecomments.source.post.Hit
import io.burba.tothecomments.source.post.HnApi
import io.burba.tothecomments.source.post.HnResponse
import retrofit2.Call
import retrofit2.mock.Calls

class FakeHnApi : HnApi {
    override fun getPosts(url: String): Call<HnResponse> = Calls.response(
        HnResponse(
            listOf(
                Hit("Example", "1234")
            )
        )
    )
}