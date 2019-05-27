package io.burba.tothecomments.support

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.mockwebserver.MockWebServer
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor that allows redirecting requests to specified hosts
 */
@Singleton
class RedirectToMockInterceptor @Inject constructor() : Interceptor {
    private val serversByHost = mutableMapOf<String, MockWebServer>()

    /**
     * @param hostName The host to redirect. Must either a regular hostname, International Domain Name, IPv4 address,
     * or IPv6 address.
     * @param mockServer The mock server to send requests that match this host to
     */
    fun redirectHostNameTo(hostName: String, mockServer: MockWebServer) {
        serversByHost[hostName] = mockServer
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalHost = originalRequest.url().host()
        val mockServer =
            serversByHost[originalHost] ?: throw IllegalStateException("No server provided for hostname $originalHost")

        val newUrl = originalRequest.url().newBuilder()
            .host(mockServer.hostName)
            .port(mockServer.port)
            .build()

        val newRequest = chain.request().newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}