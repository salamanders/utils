package info.benjaminhill.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import mu.KotlinLogging
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.time.Duration
import java.time.Instant
import java.util.*
import kotlin.concurrent.schedule

private val logger = KotlinLogging.logger {}

data class ProcessIO(
    val process: Process,
    val sendToProcess: OutputStream,
    val getFrom: InputStream,
    val getErrorFrom: InputStream,
)

/**
 * @param command All command "parts".  Things with spaces (like a file path) should not be escaped or quoted, but should be a single arg
 * @param maxDuration optional to ensure that the process ends.
 * @return process, inputStream, errorStream
 */
fun timedProcess(
    command: Array<String>, workingDir: File = File("."), maxDuration: Duration? = null
): ProcessIO {
    val process = ProcessBuilder().command(*command).directory(workingDir).start()!!
    if (maxDuration != null) {
        Timer().schedule(maxDuration.toMillis()) {
            if (process.isAlive) {
                logger.info { "Process timed out, destroying." }
                process.destroy()
            }
        }
    }
    logger.debug { "timedProcess launched: `${command.joinToString(" ")}`" }
    return ProcessIO(process, process.outputStream, process.inputStream, process.errorStream)
}


fun InputStream.toTimedLines(): Flow<Pair<Instant, String>> =
    bufferedReader().lineSequence().map { Instant.now() to it }.asFlow()
        .onStart { logger.info { "toTimedLines.onStart" } }.onCompletion {
            logger.info { "toTimedLines.onCompletion, closing InputStream" }
            this@toTimedLines.close()
        }.flowOn(Dispatchers.IO)

fun InputStream.toTimedSamples(sampleSize: Int = 4): Flow<Pair<Instant, ByteArray>> = flow<Pair<Instant, ByteArray>> {
    this@toTimedSamples.buffered().use { bufferedInputStream ->
        do {
            val ba = ByteArray(sampleSize)
            val numRead = bufferedInputStream.read(ba, 0, sampleSize)
            when (numRead) {
                sampleSize -> emit(Instant.now() to ba)
                in 1 until sampleSize -> emit(Instant.now() to ba.copyOf(numRead))
                0 -> {
                    logger.warn { "toTimedSamples should have blocked on an empty read" }
                }
                -1 -> {
                    logger.info { "toTimedSamples reached end of stream" }
                }
            }
        } while (numRead > -1)
    }
}.onStart { logger.info { "toTimedSamples.onStart" } }.onCompletion {
        logger.info { "toTimedSamples.onCompletion, closing InputStream" }
        this@toTimedSamples.close()
    }.flowOn(Dispatchers.IO)