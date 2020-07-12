package info.benjaminhill.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.io.File
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.nanoseconds
import kotlin.time.seconds


/**
 * @param command All command "parts".  Things with spaces (like a file path) should not be escaped or quoted, but should be a single arg
 * @return All of the results and errors as a single flow.  Caller must filter for the desired line in the results.
 */
@ExperimentalTime
@ExperimentalCoroutinesApi
fun runCommand(
    command: Array<String>,
    workingDir: File = File("."),
    maxDuration: Duration = 5.seconds
): Flow<String> = flow {

    val startTime = System.nanoTime().nanoseconds

    val process = ProcessBuilder()
        .redirectErrorStream(true)
        .command(*command)
        .directory(workingDir)
        .start()!!

    process.inputStream.bufferedReader().use { isr ->
        while (process.isAlive) {
            val now = System.nanoTime().nanoseconds
            if (now - startTime > maxDuration) {
                throw RuntimeException("execution timed out: $this")
            }
            isr.readLine()?.let { emit(it) }
        }
    }
}
    .buffer()
    .flowOn(Dispatchers.IO)
    .catch { println("Error while running command `${command.joinToString(" ")}`: $it") }
