package io.burba.tothecomments.http

enum class ErrorCode {
    INVALID_ARGUMENTS,
    UNEXPECTED_ERROR
}

sealed class Response
data class Success<T>(val data: T) : Response()
data class Failure(val code: ErrorCode, val message: String) : Response()