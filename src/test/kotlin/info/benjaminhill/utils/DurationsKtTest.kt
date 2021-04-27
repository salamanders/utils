package info.benjaminhill.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

internal class DurationsKtTest {

    @ExperimentalTime
    @Test
    fun testGetHms() {
        assertEquals(Duration.hours(1) + Duration.minutes(2) + Duration.seconds(3), Duration.hms("1:2:3"))
        assertEquals(Duration.minutes(2) + Duration.seconds(3), Duration.hms("2:3"))
        assertEquals(Duration.minutes(2) + Duration.seconds(3), Duration.hms("02:03"))
    }
}