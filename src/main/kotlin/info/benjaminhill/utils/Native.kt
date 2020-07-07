package info.benjaminhill.utils

import com.google.common.base.Stopwatch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.time.ExperimentalTime


/**
 * @param command All command "parts".  Things with spaces (like a file path) should not be escaped or quoted, but should be a single arg
 */
@ExperimentalTime
@ExperimentalCoroutinesApi
internal fun runCommand(
    command: Array<String>,
    workingDir: File = File(".")
): Flow<String> = flow {

    val stopwatch = Stopwatch.createStarted()

    val process = ProcessBuilder()
        .redirectErrorStream(true)
        .command(*command)
        .directory(workingDir)
        .start()!!

    process.inputStream.bufferedReader().use { isr ->
        while (process.isAlive) {
            if (stopwatch.elapsed(TimeUnit.SECONDS) > 5) {
                throw RuntimeException("execution timed out: $this")
            }
            // Nulls every time it catches up?
            isr.readLine()?.let { emit(it) }
        }
    }
}.buffer().flowOn(Dispatchers.IO)
