package io.burba.tothecomments.source.post

import io.burba.tothecomments.action.Post
import io.burba.tothecomments.source.FetchException
import java.net.URL
import javax.inject.Inject

class HnPostSource @Inject constructor(private val hnApi: HnApi) : PostSource {
    override fun postsForUrl(url: URL): List<Post> {
        val response = hnApi.getPosts(url.toString()).execute()
        if (response.isSuccessful) {
            val body = response.body() ?: throw FetchException("Received empty response body")
            return body.hits.map {
                Post(
                    "https://news.ycombinator.com/item?id=${it.objectID}",
                    it.title
                )
            }
        } else {
            throw FetchException(
                response.errorBody()?.string() ?: "Unable to fetch requested data"
            )
        }
    }
}