package info.benjaminhill.utils

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

internal class CommandsKtTest {

    @Test
    fun testRunCommand() {
        LOG.info { "testRunCommand" }

        runBlocking {
            val outputs = runCommand(arrayOf("ping", "google.com"))
            assertTrue(outputs.toList().joinToString().contains("minimum", ignoreCase = true))
        }

        runBlocking {
            assertThrows(CancellationException::class.java) {
                runBlocking {
                    runCommand(
                        command = arrayOf("ping", "google.com"),
                        maxDuration = 1.milliseconds
                    ).toList()
                }
            }
        }
    }
}
