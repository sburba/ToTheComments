package io.burba.tothecomments.util

import java.net.MalformedURLException
import java.net.URL

@Throws(MalformedURLException::class)
fun String.coerceToUrl(): URL {
    return try {
        URL(this)
    } catch (e: MalformedURLException) {
        // Most common case is it's missing the protocol, if it's not that, then just bail
        URL("http://$this")
    }
}

fun String?.toNullIfEmpty() = if (this.isNullOrEmpty()) null else this
