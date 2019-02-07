package io.burba.tothecomments

enum class ErrorCode {
    INTERNAL_SERVER_ERROR
}

data class Failure(val code: ErrorCode, val message: String)

data class UrlDetails(val meta: Meta, val posts: List<Post>)
data class Post(val url: String, val title: String, val image_url: String? = null, val category: String? = null)
data class Meta(val title: String? = null, val image_url: String? = null, val description: String? = null)
