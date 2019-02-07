package io.burba.tothecomments.source.meta

import io.burba.tothecomments.Meta
import io.burba.tothecomments.source.FetchException

class MicrolinkMetaSource(private val microlinkApi: MicrolinkApi) : MetaSource {
    override fun metaForUrl(url: String): Meta {
        val response = microlinkApi.getMeta(url).execute()
        if (response.isSuccessful) {
            val body = response.body() ?: throw FetchException("Unexpected empty response body")
            return Meta(body.data.title, body.data.image?.url, body.data.description)
        } else {
            throw FetchException(
                response.errorBody()?.string() ?: "Unable to fetch requested data"
            )
        }
    }
}