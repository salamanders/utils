package info.benjaminhill.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger

internal class LogInfrequentlyKtTest {

    @Test
    fun logInfrequently() {
        val li = LogInfrequently()
        repeat(20) {
            li.hit()
        }
    }


    @Test
    fun logExp() {
        val logCount = AtomicInteger()
        repeat(20) { i ->
            logExp(i) { "Hi: $i:${logCount.incrementAndGet()}" }
        }
        assertEquals(6, logCount.get())
    }

}