package io.burba.tothecomments.http

import io.burba.tothecomments.command.FetchPostsCommand
import io.burba.tothecomments.command.InvalidUrlException
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK

class PostsApi(private val fetchPostsCommand: FetchPostsCommand) {
    suspend fun get(url: String, respond: suspend (HttpStatusCode, Any) -> Unit) {
        try {
            respond(OK, fetchPostsCommand.run(url))
        } catch (e: InvalidUrlException) {
            respond(BadRequest, Failure(ErrorCode.INVALID_ARGUMENTS, "Invalid URL $url"))
        }
    }
}