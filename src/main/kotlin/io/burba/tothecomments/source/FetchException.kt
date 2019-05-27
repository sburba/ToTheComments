package io.burba.tothecomments.source

class FetchException(message: String = "Unable to fetch requested data", cause: Throwable? = null) :
    Exception(message, cause)