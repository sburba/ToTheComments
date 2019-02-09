package io.burba.tothecomments

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.burba.tothecomments.command.FetchPostsCommand
import io.burba.tothecomments.logging.HttpLoggingInterceptor
import io.burba.tothecomments.source.meta.MicrolinkApi
import io.burba.tothecomments.source.meta.MicrolinkMetaSource
import io.burba.tothecomments.source.post.HnApi
import io.burba.tothecomments.source.post.HnPostSource
import io.burba.tothecomments.source.post.RedditPostSource
import net.dean.jraw.http.OkHttpNetworkAdapter
import net.dean.jraw.http.UserAgent
import net.dean.jraw.oauth.Credentials
import net.dean.jraw.oauth.OAuthHelper
import okhttp3.OkHttpClient
import org.slf4j.LoggerFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.UUID
import kotlin.LazyThreadSafetyMode.PUBLICATION

class DI(private val config: Config) {
    val commands = Commands()

    val moshi: Moshi by lazy(PUBLICATION) {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    private val redditSource by lazy(PUBLICATION) {
        val creds = Credentials.userless(config.redditClientId, config.redditClientSecret, UUID.randomUUID())
        val agent = UserAgent("server", "tothecomments", VERSION, "thisnameistoolon")
        val reddit = OAuthHelper.automatic(OkHttpNetworkAdapter(agent, httpClient), creds)
        // We log our requests using OkHttp's request interceptor, so skip logging them in the reddit client
        reddit.logHttp = false

        RedditPostSource(reddit)
    }

    private val httpClient by lazy(PUBLICATION) {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor(LoggerFactory.getLogger("http.requests"), System::currentTimeMillis))
            .build()
    }

    private val baseRetrofitBuilder by lazy(PUBLICATION) {
        Retrofit.Builder()
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
    }

    private val hnSource by lazy(PUBLICATION) {
        val hnApi = baseRetrofitBuilder
            .baseUrl("https://hn.algolia.com/")
            .build()
            .create(HnApi::class.java)

        HnPostSource(hnApi)
    }

    private val microLinkSource by lazy(PUBLICATION) {
        val microlinkApi = baseRetrofitBuilder
            .baseUrl("https://api.microlink.io/")
            .build()
            .create(MicrolinkApi::class.java)

        MicrolinkMetaSource(microlinkApi)
    }

    private val postSources by lazy(PUBLICATION) { listOf(redditSource, hnSource) }

    inner class Commands {
        val fetchPosts by lazy(PUBLICATION) { FetchPostsCommand(microLinkSource, postSources) }
    }
}

