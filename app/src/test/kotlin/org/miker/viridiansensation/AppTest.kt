package org.miker.viridiansensation

import arrow.core.None
import io.kotest.matchers.shouldBe
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.junit.jupiter.api.Test

class AppTest {

    @Test
    fun `tests work`() {
        true shouldBe true
    }

    @Test
    fun `jackson none`() {
        val post = Post(1, 1, "foo", "bar", "baz", None)
        val json = postLens(post, Request(GET, "/"))
        val deserialized = postLens(json)
        deserialized shouldBe post
    }
}
