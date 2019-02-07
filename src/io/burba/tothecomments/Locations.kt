package io.burba.tothecomments

import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location

@KtorExperimentalLocationsAPI
@Location("/comment_pages")
class Posts(val url: String)