package info.benjaminhill.utils

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

internal class CommandsKtTest {

    @OptIn(ExperimentalTime::class)
    @Test
    fun testRunCommand() {
        LOG.info { "testRunCommand" }

        runBlocking {
            val outputs = runCommand(
                command = arrayOf("ping", "google.com", "-c 2"),
                maxDuration = 10.seconds
            )
            val allOutput = outputs.toList().joinToString()
            // println("OUTPUT: $allOutput")
            assertTrue(allOutput.contains("time", ignoreCase = true))
        }
    }

    @Test
    fun testRunCommandTimeout() {
        runBlocking {
            assertThrows(CancellationException::class.java) {
                runBlocking {
                    runCommand(
                        command = arrayOf("ping", "google.com"),
                        maxDuration = 1.nanoseconds
                    ).toList()
                }
            }
        }
    }
}
