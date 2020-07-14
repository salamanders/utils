package info.benjaminhill.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import java.lang.Runtime.getRuntime

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