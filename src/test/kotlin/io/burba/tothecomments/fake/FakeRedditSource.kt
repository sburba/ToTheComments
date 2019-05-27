package io.burba.tothecomments.fake

import io.burba.tothecomments.action.Post
import io.burba.tothecomments.source.post.PostSource
import java.net.URL

class FakeRedditSource : PostSource {
    override fun postsForUrl(url: URL): List<Post> {
        return listOf(
            Post(
                "https://reddit.com/r/todayilearned/comments/eqbby/til_examplecom_is_a_domain_you_cannot_register_to/",
                "TIL example.com is a domain you cannot register to have.",
                "default",
                "todayilearned"
            )
        )
    }
}