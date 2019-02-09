package io.burba.tothecomments.source.post

import io.burba.tothecomments.http.Post
import io.burba.tothecomments.source.FetchException
import java.net.URL

interface PostSource {
    @Throws(FetchException::class)
    fun postsForUrl(url: URL): List<Post>
}