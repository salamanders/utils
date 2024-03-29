package info.benjaminhill.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class FlowsKtTest {
    companion object {
        private val slowProcess = { a: Int ->
            runBlocking {
                delay(1.seconds)
                a * 2
            }
        }
    }

    @Test
    fun textZipWithNext() {
        runBlocking {
            val endVals: List<Int> = (1..5).toList().asFlow().zipWithNext { a, b -> a * 100 + b }.toList()
            assertArrayEquals(intArrayOf(102, 203, 304, 405), endVals.toIntArray())
        }
    }

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
                    delay(0.1.seconds)
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
