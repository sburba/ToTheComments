package io.burba.tothecomments.util

import okhttp3.RequestBody
import okio.Buffer
import java.nio.charset.Charset

private val UTF8 = Charset.forName("UTF8")!!

fun RequestBody.string(): String {
    val buffer = Buffer()
    val charset = this.contentType()?.charset(UTF8) ?: UTF8
    this.writeTo(buffer)

    return buffer.readString(charset)
}