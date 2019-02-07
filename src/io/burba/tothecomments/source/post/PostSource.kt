package io.burba.tothecomments.source.post

import io.burba.tothecomments.Post
import io.burba.tothecomments.source.FetchException

interface PostSource {
    @Throws(FetchException::class)
    fun postsForUrl(url: String): List<Post>
}