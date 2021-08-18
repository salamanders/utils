package info.benjaminhill.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class DisplayKtTest {

    @Test
    fun testGetRD() {
        assertEquals(" 123.457", 123.456789.r)
        assertEquals("-0.001", (-0.001).r)
        assertEquals("-5.0", (-5.0).r)
    }

    @Test
    fun testGetRF() {
        assertEquals(" 123.457", 123.456789f.r)
        assertEquals(" 0.0", (-0.00009f).r)
        assertEquals("-5.0", (-5.0f).r)
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