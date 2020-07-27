package info.benjaminhill.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.toList
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime
import kotlin.time.seconds

internal class FlowsKtTest {
    companion object {
        @ExperimentalTime
        private val slowProcess = { a: Int ->
            runBlocking {
                delay(1.seconds)
                a * 2
            }
        }
    }

    @ExperimentalTime
    @Test
    fun testPMap() {

        val execTime = measureTime {
            runBlocking {
                val endVals = (1..5).toList().asFlow().pMap(concurrencyLevel = 6) { _, startVal: Int ->
                    slowProcess(startVal)
                }.toList()
                assertEquals(listOf(2, 4, 6, 8, 10), endVals)
            }
        }
        assertTrue(execTime > 0.5.seconds)
        assertTrue(execTime < 2.seconds)
    }


    @ExperimentalTime
    @Test
    fun testFileChanges() {
        val tmpFile = File.createTempFile("testFile", "txt").apply {
            deleteOnExit()
        }
        val values = listOf("hello", "world", "world", "exit")

        Assertions.assertThrows(CancellationException::class.java) {
            runBlocking {
                launch {
                    values.forEach {
                        tmpFile.writeText(it)
                        delay(0.1.seconds)
                    }
                }
                tmpFile
                    .changesToFlow()
                    .collectIndexed { index, value ->
                        when (index) {
                            0 -> assertEquals("hello", value)
                            1 -> assertEquals("world", value)
                            2 -> assertEquals("exit", value)
                        }
                        if ("exit" == value) {
                            cancel()
                        }
                    }
            }
        }


    }
}