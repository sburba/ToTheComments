package io.burba.tothecomments.source.meta

import io.burba.tothecomments.action.Meta
import io.burba.tothecomments.source.FetchException
import io.burba.tothecomments.util.toNullIfEmpty
import java.net.URL
import javax.inject.Inject

class MicrolinkMetaSource @Inject constructor(private val microlinkApi: MicrolinkApi) : MetaSource {
    override fun metaForUrl(url: URL): Meta {
        val response = microlinkApi.getMeta(url.toString()).execute()
        if (response.isSuccessful) {
            val body = response.body() ?: throw FetchException("Unexpected empty response body")
            return Meta(
                body.data.title.toNullIfEmpty(),
                body.data.image?.url.toNullIfEmpty(),
                body.data.description.toNullIfEmpty()
            )
        } else {
            throw FetchException(
                response.errorBody()?.string().toNullIfEmpty()
                    ?: "Microlink request failed (${response.code()} ${response.message()})"
            )
        }
    }
}