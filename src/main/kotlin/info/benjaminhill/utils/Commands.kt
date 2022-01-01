package info.benjaminhill.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.time.withTimeout
import java.io.File
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

/**
 * @param command All command "parts".  Things with spaces (like a file path) should not be escaped or quoted, but should be a single arg
 * @return All the results and errors as a single flow.  Caller must filter for the desired line in the results.
 */
suspend fun runCommand(
    command: Array<String>,
    workingDir: File = File("."),
    maxDuration: Duration = 5.seconds
): Flow<String> = withTimeout(duration = maxDuration.toJavaDuration()) {
    val timeChecker: Job
    ProcessBuilder()
        .redirectErrorStream(true)
        .command(*command)
        .directory(workingDir)
        .start()!!.also { process ->
            // Because you can't expect newlines to happen if the process locked up.
            timeChecker = launch {
                val startTime = System.nanoTime().nanoseconds
                val sleepDuration = maxDuration / 20
                while (process.isAlive) {
                    if (System.nanoTime().nanoseconds - startTime > maxDuration) {
                        process.destroyForcibly()
                        throw CancellationException("Ran over time: $maxDuration")
                    }
                    delay(sleepDuration)
                }
            }
        }
        .inputStream
        .bufferedReader()
        .lineSequence()
        .asFlow()
        .buffer()
        .flowOn(Dispatchers.IO)
        .onCompletion {
            timeChecker.cancel() // redundant
        }
        .catch { error ->
            // Something worse than the Error Stream (like unknown command)
            error("Error while running command `${command.joinToString(" ")}`: $error")
        }
}
