package io.burba.tothecomments.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun <T> asyncIO(block: suspend CoroutineScope.() -> T): T = withContext(Dispatchers.IO, block)
