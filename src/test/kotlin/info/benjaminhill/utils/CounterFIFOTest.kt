package info.benjaminhill.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class CounterFIFOTest {

    @Test
    fun topN() {
        val counter = CounterFIFO(100)
        repeat(30) {
            counter.inc("a")
            counter.inc("a")
            counter.inc("a")
            counter.inc("b")
        }
        assertEquals(setOf("a"), counter.topN(1))
        assertEquals(setOf("a", "b"), counter.topN(2))

        repeat(200) {
            counter.inc("c")
        }
        assertEquals(setOf("c"), counter.topN(1))
    }
}