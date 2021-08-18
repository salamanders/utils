package info.benjaminhill.utils

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

internal class CommandsKtTest {

    @ExperimentalTime
    @Test
    fun testRunCommand() {
        LOG.info { "testRunCommand" }

        runBlocking {
            val outputs = runCommand(arrayOf("echo", "hi there")).toList()
            assertEquals(1, outputs.size)
            assertEquals("hi there", outputs[0])
        }

        runBlocking {
            assertThrows(CancellationException::class.java) {
                runBlocking {
                    runCommand(
                        command = arrayOf("sleep", "3"),
                        maxDuration = Duration.seconds(1)
                    ).toList()
                }
            }
        }
    }
}
