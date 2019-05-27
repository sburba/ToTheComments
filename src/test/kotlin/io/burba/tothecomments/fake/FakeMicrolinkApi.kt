package io.burba.tothecomments.fake

import io.burba.tothecomments.source.meta.MicrolinkApi
import io.burba.tothecomments.source.meta.MicrolinkMeta
import io.burba.tothecomments.source.meta.MicrolinkResponse
import retrofit2.Call
import retrofit2.mock.Calls

class FakeMicrolinkApi : MicrolinkApi {
    override fun getMeta(url: String): Call<MicrolinkResponse> = Calls.response(MicrolinkResponse(MicrolinkMeta("Example Title", null, null)))
}