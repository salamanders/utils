package info.benjaminhill.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File
import java.lang.Runtime.getRuntime
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.TimeMark
import kotlin.time.TimeSource.Monotonic
import kotlin.time.seconds

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
    slowAfterDuration: Duration = 5.seconds,
    slowPoll: Duration = 0.5.seconds,
    fastPoll: Duration = 0.01.seconds
): Flow<String> = flow<ByteArray> {
    require(exists()) { "File does not exist: '${absolutePath}'" }
    require(canRead()) { "Can not read file: '${absolutePath}'" }
    val filePath = this@changesToFlow.toPath()

    while (true) { // Cancellable
        emit(Files.readAllBytes(filePath))
        delay(
            if (lastModified.get().elapsedNow() > slowAfterDuration) {
                slowPoll
            } else {
                fastPoll
            }
        )
    }
}
    .flowOn(Dispatchers.IO) // Run in background
    .distinctUntilChanged { old, new -> old.contentEquals(new) } // no duplicate status
    .onEach { lastModified.set(Monotonic.markNow()) }
    .conflate() // Immediately provide most recent
    .map { String(it, charset).trim() }