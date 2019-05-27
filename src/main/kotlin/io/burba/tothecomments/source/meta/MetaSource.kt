package io.burba.tothecomments.source.meta

import io.burba.tothecomments.action.Meta
import java.net.URL

interface MetaSource {
    fun metaForUrl(url: URL): Meta
}