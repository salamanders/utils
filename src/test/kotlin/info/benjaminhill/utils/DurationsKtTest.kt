package info.benjaminhill.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

internal class DurationsKtTest {

    @Test
    fun testGetHms() {
        assertEquals(1.hours + 2.minutes + 3.seconds, "1:2:3".hms)
        assertEquals(2.minutes + 3.seconds, "2:3".hms)
        assertEquals(2.minutes + 3.seconds, "02:03".hms)
        assertEquals(0.seconds, "".hms)
    }
}