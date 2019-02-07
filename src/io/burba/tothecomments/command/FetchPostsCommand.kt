package io.burba.tothecomments.command

import io.burba.tothecomments.Meta
import io.burba.tothecomments.UrlDetails
import io.burba.tothecomments.source.meta.MetaSource
import io.burba.tothecomments.source.post.PostSource
import io.burba.tothecomments.util.asyncIO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

class FetchPostsCommand(private val metaSources: Iterable<MetaSource>, private val postSources: Iterable<PostSource>) {
    suspend fun run(url: String): UrlDetails = asyncIO {
        val metaDeferred = metaSources.map { source -> async { source.metaForUrl(url) } }
        val postDeferred = postSources.map { source -> async { source.postsForUrl(url) } }
        val meta = bestMeta(metaDeferred.awaitAll())
        val posts = postDeferred.awaitAll().flatten()

        UrlDetails(meta, posts)
    }

    private fun bestMeta(metas: Iterable<Meta>): Meta {
        return metas.fold(Meta()) { best, current ->
            best.copy(
                title = if (best.title.isNullOrBlank()) current.title else best.title,
                image_url = if (best.image_url.isNullOrBlank()) current.image_url else best.image_url,
                description = if (best.description.isNullOrBlank()) current.image_url else best.description
            )
        }
    }
}