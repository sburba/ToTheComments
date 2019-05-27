package io.burba.tothecomments

const val VERSION = "0.0.1"

data class Config(
    val redditClientId: String,
    val redditClientSecret: String,
    val port: Int,
    val hnApiBaseUrl: String = "https://hn.algolia.com/",
    val microlinkApiBaseUrl: String = "https://api.microlink.io/"
)
