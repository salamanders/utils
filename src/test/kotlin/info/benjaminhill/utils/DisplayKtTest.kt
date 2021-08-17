package info.benjaminhill.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class DisplayKtTest {

    @Test
    fun testGetR() {
        assertEquals(" 123.4568", 123.456789.r)
        assertEquals("-0.00010", (-0.00009).r)
        assertEquals("-5.0", (-5.0).r)
    }

    @Test
    fun testDeepStackTrace() {
        try {
            val foo = listOf<List<String>>()
            foo.first()
        } catch (e: NoSuchElementException) {
            e.printDeepStackTrace()
        }
    }
}