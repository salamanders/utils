package info.benjaminhill.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*

/**
 * Map in parallel
 */
suspend fun <T, R> Flow<T>.concurrentIndexedMap(
    concurrencyLevel: Int = Runtime.getRuntime().availableProcessors(),
    transform: suspend (Int, T) -> R,
) = scopedConcurrentIndexedMap(
    scope = CoroutineScope(currentCoroutineContext()),
    concurrencyLevel = concurrencyLevel,
    transform = transform,
)

private fun <T, R> Flow<T>.scopedConcurrentIndexedMap(
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