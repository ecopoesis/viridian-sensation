package org.miker.viridiansensation

import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class AppTest {

    @Test
    fun `tests work`() {
        true shouldBe true
    }

    @Test
    fun `example`() {
        val events = listOf(
            Event("/pages/a-big-river", "d1177368-2310-11e8-9e2a-9b860a0d9039", 1512754583000),
            Event("/pages/a-small-dog", "d1177368-2310-11e8-9e2a-9b860a0d9039", 1512754631000),
            Event("/pages/a-big-talk", "f877b96c-9969-4abc-bbe2-54b17d030f8b", 1512709065294),
            Event("/pages/a-sad-story", "f877b96c-9969-4abc-bbe2-54b17d030f8b", 1512711000000),
            Event("/pages/a-big-river", "d1177368-2310-11e8-9e2a-9b860a0d9039", 1512754436000),
            Event("/pages/a-sad-story", "f877b96c-9969-4abc-bbe2-54b17d030f8b", 1512709024000)
        )

        val app = App()
        val sessions = app.process(events)

        sessions shouldContainExactly mapOf(
            "f877b96c-9969-4abc-bbe2-54b17d030f8b" to listOf(
                Session(41294, listOf("/pages/a-sad-story", "/pages/a-big-talk"), 1512709024000),
                Session(0, listOf("/pages/a-sad-story"), 1512711000000)
            ),
            "d1177368-2310-11e8-9e2a-9b860a0d9039" to listOf(
                Session(195000, listOf("/pages/a-big-river", "/pages/a-big-river", "/pages/a-small-dog"), 1512754436000)
            )
        )
    }
}
