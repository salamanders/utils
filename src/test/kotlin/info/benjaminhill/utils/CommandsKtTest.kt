package info.benjaminhill.utils

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

private val logger = KotlinLogging.logger {}

internal class CommandsKtTest {

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    @Test
    fun testRunCommand() {
        logger.info { "testRunCommand" }

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
                        maxDuration = 1.seconds
                    ).toList()
                }
            }
        }
    }
}
