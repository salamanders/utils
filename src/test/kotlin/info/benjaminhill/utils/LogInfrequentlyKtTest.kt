package info.benjaminhill.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.ExperimentalTime

internal class LogInfrequentlyKtTest {

    @ExperimentalTime
    @Test
    fun logInfreq() {
        val linf = LogInfrequently()
        repeat(20) {
            linf.hit()
        }
    }


    @Test
    fun logexp() {
        val logCount = AtomicInteger()
        repeat(20) { i ->
            logexp(i) { "Hi: $i:${logCount.incrementAndGet()}" }
        }
        assertEquals(6, logCount.get())
    }

}