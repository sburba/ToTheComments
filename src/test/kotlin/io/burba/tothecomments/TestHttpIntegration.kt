package io.burba.tothecomments

import io.burba.tothecomments.di.AppModule
import io.burba.tothecomments.di.DaggerTestDI
import io.burba.tothecomments.support.ToTheCommentsApi
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.test.assertEquals

object TestHttpIntegration : Spek({
    val api by memoized {
        Retrofit.Builder()
            .baseUrl("http://localhost:8585")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ToTheCommentsApi::class.java)
    }

    val deps by memoized {
        DaggerTestDI.builder()
            .appModule(
                AppModule(
                    Config(
                        redditClientId = "INVALID",
                        redditClientSecret = "INVALID",
                        port = 8585
                    )
                )
            )
            .build()
    }

    val app by lazy { deps.app() }
    val changeHostInterceptor = deps.changeHostInterceptor()

//    val redditPostSuccessResponse by memoized {
//        this.javaClass.getResource("reddit-posts-success-response.json").readText()
//    }

    val hnServer = MockWebServer()
    val redditServer = MockWebServer()
    val microlinkServer = MockWebServer()
    val servers = arrayOf(hnServer, redditServer, microlinkServer)

    beforeEachTest {
        changeHostInterceptor.redirectHostNameTo("www.reddit.com", redditServer)
        changeHostInterceptor.redirectHostNameTo("oauth.reddit.com", redditServer)
        changeHostInterceptor.redirectHostNameTo("api.microlink.io", microlinkServer)
        changeHostInterceptor.redirectHostNameTo("hn.algolia.com", hnServer)
        servers.forEach { it.start() }

        app.start()
    }

    afterEachTest {
        app.stop()
        servers.forEach { it.shutdown() }
    }

    describe("/posts/:name") {
        it("returns 400 when given an invalid url") {
            redditServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody("""{"access_token": "INVALID_ACCESS_TOKEN", "token_type": "bearer", "expires_in": 3600, "scope": "*"}""")
            )

            redditServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody("test")
            )

            microlinkServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody("test")
            )

            hnServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody("test")
            )

            val result = api.getMeta("a").execute()
            assertEquals(400, result.code())
        }
    }
})