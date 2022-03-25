package info.benjaminhill.utils

import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.stream.consumeAsFlow
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.UncheckedIOException
import java.time.Duration
import kotlin.test.assertEquals

internal class CommandsKtTest {

    @Test
    fun testTimedProcess() {
        println("testRunCommand")
        runBlocking {
            val processIO = timedProcess(
                command = arrayOf("ping", "google.com", "-c 2"),
                maxDuration = Duration.ofSeconds(10)
            )
            val allOutput = processIO.getFrom.bufferedReader().readLines().joinToString("\n")
            // println("OUTPUT: $allOutput")
            assertTrue(allOutput.contains("time", ignoreCase = true))
        }
    }

    @Test
    fun testRunCommandTimeout() {
        runBlocking {
            assertThrows(UncheckedIOException::class.java) {
                runBlocking {
                    val processIO = timedProcess(
                        command = arrayOf("ping", "google.com"),
                        maxDuration = Duration.ofNanos(1)
                    )
                    processIO.getFrom.bufferedReader().lines().consumeAsFlow().collect {
                        println(it)
                    }
                }
            }
        }
    }

    @Test
    fun toTimedLines() {
        runBlocking {
            val processIO = timedProcess(
                command = arrayOf("ping", "google.com", "-c 2"),
                maxDuration = Duration.ofSeconds(10)
            )
            val (_, line) = processIO.getFrom.toTimedLines()
                .filter { it.second.contains("data bytes") }
                .first()
            assertTrue(line.contains("bytes"))

        }

        @Test
        fun toTimedSamples() {
            runBlocking {
                val processIO = timedProcess(
                    command = arrayOf("cat", getFile("README.md").toString())
                )
                processIO.getFrom.toTimedSamples(
                    sampleSize = 8,
                ).take(1).collect { (instant, chunk) ->
                    println("Instant: $instant, chunk:$chunk")
                    assertEquals(8, chunk.size)
                }
            }
        }
    }
}
