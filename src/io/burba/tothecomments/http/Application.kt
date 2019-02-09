package io.burba.tothecomments.http

import com.ryanharter.ktor.moshi.moshi
import io.burba.tothecomments.Config
import io.burba.tothecomments.DI
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.AutoHeadResponse
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.features.deflate
import io.ktor.features.gzip
import io.ktor.features.minimumSize
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.slf4j.event.Level
import java.util.UUID

private val config = Config()
private val deps = DI(config)
private val apis = deps.apis

fun main() {
    embeddedServer(
        Netty,
        port = config.port,
        watchPaths = listOf("tothecomments"),
        module = Application::module
    ).start(true)
}

fun Application.module() {
    install(Compression) {
        gzip {
            priority = 1.0
        }
        deflate {
            priority = 10.0
            minimumSize(1024) // condition
        }
    }

    install(AutoHeadResponse)
    install(ContentNegotiation) { moshi(deps.moshi) }

    install(CallLogging) {
        level = Level.INFO
        mdc("request-id") { UUID.randomUUID().toString() }
    }

    routing {
        get("/posts/{url}") { apis.posts.get(call.parameters["url"]!!, call::respond) }

        install(StatusPages) {
            exception<Throwable> {
                call.application.log.error("Unexpected error", it)
                call.respond(
                    HttpStatusCode.ServiceUnavailable,
                    Failure(ErrorCode.INTERNAL_SERVER_ERROR, "An unexpected error has occurred")
                )
            }
        }
    }
}

