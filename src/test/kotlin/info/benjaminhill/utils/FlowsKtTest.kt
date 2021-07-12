package info.benjaminhill.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

internal class FlowsKtTest {
    companion object {
        @ExperimentalTime
        private val slowProcess = { a: Int ->
            runBlocking {
                delay(Duration.seconds(1))
                a * 2
            }
        }
    }

    @ExperimentalTime
    @Test
    fun testPMap() {
        val execTime = measureTime {
            runBlocking {
                val endVals = (1..5).toList().asFlow().mapConcurrently { startVal: Int ->
                    slowProcess(startVal)
                }.toList()
                assertEquals(listOf(2, 4, 6, 8, 10), endVals)
            }
        }
        assertTrue(execTime > Duration.seconds(0.5))
        assertTrue(execTime < Duration.seconds(2))
    }


    @ExperimentalTime
    @Test
    fun testFileChanges() {
        var maxSeen = 0
        val tmpFile = File.createTempFile("testFile2", "txt").apply {
            deleteOnExit()
        }
        val values = listOf("hello", "world", "world", "exit")
        val uniqueValues = values.distinct()

        runBlocking(Dispatchers.IO) {
            launch {
                values.forEach {
                    delay(Duration.seconds(0.1))
                    tmpFile.writeText(it)
                }
            }
            // If you don't wrap it in a launch, it should throw a CancellationException
            launch {
                tmpFile
                    .changesToFlow()
                    .filter { it.isNotBlank() }
                    .catch { t ->
                        println("Hit a snag: $t")
                    }
                    .collectIndexed { index, value ->
                        println("$index $value")
                        maxSeen++
                        assertEquals(uniqueValues[index], value)
                        if ("exit" == value) {
                            cancel()
                        }
                    }
            }
        }.also {
            assertEquals(uniqueValues.size, maxSeen)
        }
    }
}
