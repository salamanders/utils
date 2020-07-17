package info.benjaminhill.utils

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
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
}