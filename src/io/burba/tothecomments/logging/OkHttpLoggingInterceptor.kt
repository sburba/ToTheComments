package io.burba.tothecomments.logging

import io.burba.tothecomments.util.string
import net.logstash.logback.argument.StructuredArguments.fields
import okhttp3.Interceptor
import okhttp3.Response
import org.slf4j.Logger

data class RequestLog(val url: String, val took_ms: Long, val method: String)
data class FailedRequestLog(
    val url: String,
    val took_ms: Long,
    val method: String,
    val requestBody: String,
    val responseBody: String
)

class HttpLoggingInterceptor(private val logger: Logger, private val timeInMillis: () -> Long) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val method = request.method()
        val url = request.url().toString()

        val startTime = timeInMillis()
        val response = chain.proceed(request)
        val tookMs = timeInMillis() - startTime

        if (response.isSuccessful) {
            logger.info("Successful request", fields(RequestLog(url, tookMs, method)))
        } else {
            logger.warn("Failed request", fields(FailedRequestLog(
                url = url,
                took_ms = tookMs,
                method = method,
                requestBody = request.body()?.string() ?: "<empty>",
                responseBody = response.body()?.string() ?: "<empty>"
            )))
        }

        return response
    }
}