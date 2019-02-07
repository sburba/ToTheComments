package io.burba.tothecomments.source.meta

import io.burba.tothecomments.Meta

interface MetaSource {
    fun metaForUrl(url: String): Meta
}