package io.burba.tothecomments

import io.burba.tothecomments.di.AppModule
import io.burba.tothecomments.di.DaggerDI
import io.burba.tothecomments.http.App
import org.slf4j.LoggerFactory
import kotlin.system.measureTimeMillis

fun main() {
    val config = Config(
        redditClientId = System.getenv("REDDIT_CLIENT_ID"),
        redditClientSecret = System.getenv("REDDIT_CLIENT_SECRET"),
        port = System.getenv("PORT")?.toInt() ?: 8080
    )

    DaggerDI.builder()
        .appModule(AppModule(config))
        .build()
        .app()
        .start()
}