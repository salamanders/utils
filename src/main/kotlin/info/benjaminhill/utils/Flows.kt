package info.benjaminhill.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File
import java.io.RandomAccessFile
import java.lang.Runtime.getRuntime
import java.nio.charset.Charset
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.TimeMark
import kotlin.time.TimeSource.Monotonic


/**
 * Map in parallel.
 * @param concurrencyLevel 0 would be a standard map.  Don't exceed your cores - 1 unless you really mean it.
 */
suspend fun <T, R> Flow<T>.pMap(
    concurrencyLevel: Int = (getRuntime().availableProcessors() - 1).coerceAtLeast(0),
    transform: suspend (Int, T) -> R,
) = scopedPMap(
    scope = CoroutineScope(currentCoroutineContext()),
    concurrencyLevel = concurrencyLevel,
    transform = transform,
)

private fun <T, R> Flow<T>.scopedPMap(
    scope: CoroutineScope,
    concurrencyLevel: Int,
    transform: suspend (Int, T) -> R,
): Flow<R> = this
    .withIndex()
    .map { scope.async { transform(it.index, it.value) } }
    .buffer(concurrencyLevel)
    .map { it.await() }

fun <T, R> Flow<T>.zipWithNext(transform: (a: T, b: T) -> R): Flow<R> = flow {
    var last: T? = null
    this@zipWithNext.collect { elt ->
        last?.let {
            emit(transform(it, elt))
        }
        last = elt
    }
}

/**
 * Actively polls a file for changes,
 * creating a flow of the contents every time it changes.
 * Best for one-line status files.
 * TODO: Refactor into a StateFlow
 */
@ExperimentalTime
fun File.changesToFlow(
    charset: Charset = Charset.defaultCharset(),
    lastModified: AtomicReference<TimeMark> = AtomicReference(Monotonic.markNow()),
): Flow<String> = flow {
    val contents = ByteArray(this@changesToFlow.length().coerceAtLeast(1_024).toInt())
    var position: Int
    var bytesRead: Int

    @Suppress("BlockingMethodInNonBlockingContext")
    RandomAccessFile(this@changesToFlow, "r").use { raf ->
        while (true) {
            position = 0
            raf.seek(0)
            do {
                bytesRead = raf.read(contents, position, contents.size - position)
                if (bytesRead > 0) {
                    position += bytesRead
                }
            } while (bytesRead != -1 && position < contents.size)
            emit(contents.copyOfRange(0, position))
            delay(
                (lastModified.get().elapsedNow().toDouble(DurationUnit.MILLISECONDS).toLong() / 20).coerceIn(1L..500L)
            )
        }
    }
}
    .flowOn(Dispatchers.IO) // Run in background
    .distinctUntilChanged { old, new -> old.contentEquals(new) } // no duplicate status
    .onEach { lastModified.set(Monotonic.markNow()) } // reset the delay timer
    .conflate() // Immediately provide most recent
    .map { String(it, charset).trim() }