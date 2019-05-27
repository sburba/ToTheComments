package io.burba.tothecomments.action

import io.burba.tothecomments.source.meta.MetaSource
import io.burba.tothecomments.source.post.PostSource
import io.burba.tothecomments.util.coerceToUrl
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.net.MalformedURLException
import javax.inject.Inject

class InvalidUrlException(cause: Throwable) : Exception(cause)

data class UrlDetails(val meta: Meta, val posts: List<Post>)
data class Post(val url: String, val title: String, val image_url: String? = null, val category: String? = null)
data class Meta(val title: String? = null, val image_url: String? = null, val description: String? = null)

class FetchPostsAction @Inject constructor(
    private val metaSource: MetaSource,

    // Kotlin jvm interop generates wildcards for the PostSource interface part of the parameter type, which means
    // dagger doesn't see our provider which doesn't have generated wildcards, so we have to explicitly suppress the
    // wildcards for the types to line up :(
    // See https://stackoverflow.com/a/45461344 for more details
    private val postSources: Iterable<@JvmSuppressWildcards PostSource>
) {
    @Throws(InvalidUrlException::class)
    suspend fun run(urlStr: String): UrlDetails = coroutineScope {
        val url = try {
            urlStr.coerceToUrl()
        } catch (e: MalformedURLException) {
            throw InvalidUrlException(e)
        }

        val metaAsync = async { metaSource.metaForUrl(url) }
        val postsAsync = postSources.map { source -> async { source.postsForUrl(url) } }

        val meta = metaAsync.await()
        val posts = postsAsync.awaitAll().flatten()

        UrlDetails(meta, posts)
    }
}