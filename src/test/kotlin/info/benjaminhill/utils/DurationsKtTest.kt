package info.benjaminhill.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.time.ExperimentalTime
import kotlin.time.hours
import kotlin.time.minutes
import kotlin.time.seconds

internal class DurationsKtTest {

    @ExperimentalTime
    @Test
    fun testGetHms() {
        assertEquals(1.hours + 2.minutes + 3.seconds, "1:2:3".hms)
        assertEquals(2.minutes + 3.seconds, "2:3".hms)
        assertEquals(2.minutes + 3.seconds, "02:03".hms)
    }
}