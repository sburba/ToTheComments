package io.burba.tothecomments.http

import com.google.gson.Gson
import io.burba.tothecomments.Config
import io.burba.tothecomments.http.api.PostsApi
import io.javalin.Javalin
import io.javalin.json.FromJsonMapper
import io.javalin.json.JavalinJson
import io.javalin.json.ToJsonMapper
import kotlinx.coroutines.runBlocking
import net.logstash.logback.marker.Markers.append
import net.logstash.logback.marker.Markers.appendEntries
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.util.UUID
import javax.inject.Inject

class App @Inject constructor(private val config: Config, gson: Gson, postsApi: PostsApi) {
    private val app: Javalin

    init {
        val logger = LoggerFactory.getLogger("io.burba.tothecomments.Application")

        JavalinJson.toJsonMapper = object : ToJsonMapper {
            override fun map(obj: Any): String = gson.toJson(obj)
        }

        JavalinJson.fromJsonMapper = object : FromJsonMapper {
            override fun <T> map(json: String, targetClass: Class<T>) = gson.fromJson(json, targetClass)
        }

        app = Javalin.create().apply {
            disableStartupBanner()

            requestLogger { ctx, executionTimeMs ->
                logger.info(
                    appendEntries(
                        mapOf(
                            "time_ms" to executionTimeMs,
                            "path" to ctx.matchedPath(),
                            "method" to ctx.method()
                        )
                    ),
                    "Request Completed"
                )
            }

            before {
                // Add a unique request id to all logs for each request
                MDC.put("request_id", UUID.randomUUID().toString())
            }

            exception(Exception::class.java) { e, ctx ->
                val trace = e.stackTrace.map { "${it.fileName}.${it.methodName}:${it.lineNumber}" }
                logger.error(append("trace", trace), e.message)
                ctx.status(500)
                ctx.json(Failure(ErrorCode.UNEXPECTED_ERROR, "An unexpected error has occurred"))
            }

            get("posts/:url") { ctx ->
                runBlocking {
                    val url = ctx.pathParam<String>("url").get()
                    val result = postsApi.fetch(url)
                    ctx.status(result.status)
                    ctx.json(result.body)
                }
            }
        }
    }

    fun start() {
        app.start(config.port)
    }

    fun stop() {
        app.stop()
    }
}
