package io.burba.tothecomments.http.api

import io.burba.tothecomments.action.FetchPostsAction
import io.burba.tothecomments.action.InvalidUrlException
import io.burba.tothecomments.http.ErrorCode
import io.burba.tothecomments.http.Failure
import io.burba.tothecomments.http.HttpResponse
import io.burba.tothecomments.http.Success
import javax.inject.Inject

class PostsApi @Inject constructor(private val fetchPostsAction: FetchPostsAction) {
    suspend fun fetch(url: String): HttpResponse {
        return try {
            HttpResponse(200, Success(fetchPostsAction.run(url)))
        } catch (e: InvalidUrlException) {
            HttpResponse(
                400,
                Failure(
                    ErrorCode.INVALID_ARGUMENTS,
                    "Invalid URL $url"
                )
            )
        }
    }
}