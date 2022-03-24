package info.benjaminhill.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.io.File
import java.io.InputStream
import java.time.Duration
import java.time.Instant
import java.util.*
import kotlin.concurrent.schedule

/**
 * @param command All command "parts".  Things with spaces (like a file path) should not be escaped or quoted, but should be a single arg
 * @param maxDuration optional to ensure that the process ends.
 * @return inputStream to errorStream
 */
fun timedProcess(
    command: Array<String>,
    workingDir: File = File("."),
    maxDuration: Duration? = null
): Pair<InputStream, InputStream> =
    ProcessBuilder()
        .command(*command)
        .directory(workingDir)
        .start()!!.let { process ->
            if (maxDuration != null) {
                Timer().schedule(maxDuration.toMillis()) {
                    if (process.isAlive) {
                        process.destroy()
                    }
                }
            }

            LOG.debug { "timedProcess launched: `${command.joinToString(" ")}`" }
            process.inputStream to process.errorStream
        }


fun InputStream.toTimedLines(): Flow<Pair<Instant, String>> =
    bufferedReader()
        .lineSequence()
        .map { Instant.now() to it }
        .asFlow()
        .onStart { LOG.info { "toTimedLines.onStart" } }
        .onCompletion {
            LOG.info { "toTimedLines.onCompletion, closing InputStream" }
            this@toTimedLines.close()
        }.flowOn(Dispatchers.IO)

fun InputStream.toTimedSamples(sampleSize: Int = 4): Flow<Pair<Instant, ByteArray>> =
    flow {
        val bufferedInputStream = this@toTimedSamples.buffered()
        do {
            val sample = bufferedInputStream.readNBytes(sampleSize)
            emit(Instant.now() to sample)
        } while (sample.size == sampleSize)
    }
        .onStart { LOG.info { "toTimedSamples.onStart" } }
        .onCompletion {
            LOG.info { "toTimedSamples.onCompletion, closing InputStream" }
            this@toTimedSamples.close()
        }
        .flowOn(Dispatchers.IO)