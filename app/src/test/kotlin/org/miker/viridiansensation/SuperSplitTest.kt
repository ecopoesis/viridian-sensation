package org.miker.viridiansensation

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import org.junit.jupiter.api.Test
import java.lang.Math.abs

class SuperSplitTest {

    @Test
    fun `single split`() {
        val list = listOf(1, 2, 4)
        val result = list.superSplit { a, b -> abs(b - a ) > 1 }
        result shouldContainExactly listOf(listOf(1, 2), listOf(4))
    }

    @Test
    fun `no split`() {
        val list = listOf(1, 2, 3)
        val result = list.superSplit { a, b -> abs(b - a ) > 1 }
        result shouldContainExactly listOf(listOf(1, 2, 3))
    }

    @Test
    fun `empty list`() {
        val list = emptyList<Int>()
        val result = list.superSplit { a, b -> abs(b - a ) > 1 }
        result.shouldBeEmpty()
    }
}
