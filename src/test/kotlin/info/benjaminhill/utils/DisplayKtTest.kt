package info.benjaminhill.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class DisplayKtTest {

    @Test
    fun testGetR() {
        assertEquals(" 123.4568", 123.456789.r)
        assertEquals("-0.0001", (-0.00009).r)
        assertEquals("-5.0000", (-5.0).r)
    }
}