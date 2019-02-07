package io.burba.tothecomments

const val VERSION = "0.0.1"

data class Config(
    val redditClientId: String = System.getenv("REDDIT_CLIENT_ID"),
    val redditClientSecret: String = System.getenv("REDDIT_CLIENT_SECRET"),
    val port: Int = System.getenv("PORT")?.toInt() ?: 8080
)
