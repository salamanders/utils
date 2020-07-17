package info.benjaminhill.utils

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class FunsKtTest {

    @Test
    fun testRetryOrNull() {


        runBlocking {
            var count = 0
            val output = retryOrNull {
                count++
                return@retryOrNull if (count == 2) {
                    "worked"
                } else {
                    null
                }
            }
            assertEquals("worked", output)
        }

        runBlocking {
            var count = 0
            val output = retryOrNull {
                count++
                return@retryOrNull if (count == 3) {
                    "worked"
                } else {
                    null
                }
            }
            assertTrue(output == null)
        }

    }
}