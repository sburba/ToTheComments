package io.burba.tothecomments.di

import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import io.burba.tothecomments.Config
import io.burba.tothecomments.VERSION
import io.burba.tothecomments.http.App
import io.burba.tothecomments.http.api.PostsApi
import io.burba.tothecomments.logging.HttpLoggingInterceptor
import io.burba.tothecomments.source.meta.MetaSource
import io.burba.tothecomments.source.meta.MicrolinkApi
import io.burba.tothecomments.source.meta.MicrolinkMetaSource
import io.burba.tothecomments.source.post.HnApi
import io.burba.tothecomments.source.post.HnPostSource
import io.burba.tothecomments.source.post.PostSource
import io.burba.tothecomments.source.post.RedditPostSource
import net.dean.jraw.RedditClient
import net.dean.jraw.http.OkHttpNetworkAdapter
import net.dean.jraw.http.UserAgent
import net.dean.jraw.oauth.Credentials
import net.dean.jraw.oauth.OAuthHelper
import okhttp3.OkHttpClient
import org.slf4j.LoggerFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID
import javax.inject.Singleton

@Module
class AppModule(private val config: Config) {
    @Provides @Singleton fun gson() = Gson()

    @Provides @Singleton fun retrofitBuilder(httpClient: OkHttpClient, gson: Gson): Retrofit.Builder =
        Retrofit.Builder()
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())

    @Provides @Singleton fun hnApi(retrofitBuilder: Retrofit.Builder): HnApi = retrofitBuilder
        .baseUrl("https://hn.algolia.com/")
        .build()
        .create(HnApi::class.java)

    @Provides @Singleton fun microlinkApi(retrofitBuilder: Retrofit.Builder): MicrolinkApi = retrofitBuilder
        .baseUrl("https://api.microlink.io/")
        .build()
        .create(MicrolinkApi::class.java)

    @Provides @Singleton fun redditApi(httpClient: OkHttpClient): RedditClient {
        val creds = Credentials.userless(config.redditClientId, config.redditClientSecret, UUID.randomUUID())
        val agent = UserAgent("server", "tothecomments", VERSION, "thisnameistoolon")

        return OAuthHelper.automatic(OkHttpNetworkAdapter(agent, httpClient), creds).apply {
            // We log our requests using OkHttp's request interceptor, so skip logging them in the reddit client
            logHttp = false
        }
    }

    @Provides @Singleton fun postSources(hn: HnPostSource, reddit: RedditPostSource): Iterable<PostSource> =
        arrayListOf(hn, reddit)

    @Provides @Singleton fun metaSource(microlink: MicrolinkMetaSource): MetaSource = microlink

    @Provides @Singleton fun app(gson: Gson, postsApi: PostsApi): App = App(config, gson, postsApi)
}