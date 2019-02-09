package io.burba.tothecomments.http

import com.ryanharter.ktor.moshi.moshi
import io.burba.tothecomments.Config
import io.burba.tothecomments.DI
import io.burba.tothecomments.command.InvalidUrlException
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

val config = Config()
val deps = DI(config)
val commands = deps.commands

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

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Get)
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    routing {
        get("/posts/{url}") {
            // "url" is guaranteed to be there, because of the route
            val url = call.parameters["url"]!!
            try {
                call.respond(commands.fetchPosts.run(url))
            } catch (e: InvalidUrlException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    Failure(ErrorCode.INVALID_ARGUMENTS, "Invalid URL $url")
                )
            }
        }

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

