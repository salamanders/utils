package info.benjaminhill.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*

/**
 * Map in parallel
 */
suspend fun <T, R> Flow<T>.concurrentMap(
    concurrencyLevel: Int = 6,
    transform: suspend (T) -> R,
) = concurrentMap(
    scope = CoroutineScope(currentCoroutineContext()),
    concurrencyLevel = concurrencyLevel,
    transform = transform,
)

private fun <T, R> Flow<T>.concurrentMap(
    scope: CoroutineScope,
    concurrencyLevel: Int = 6,
    transform: suspend (T) -> R,
): Flow<R> = this
    .map { scope.async { transform(it) } }
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