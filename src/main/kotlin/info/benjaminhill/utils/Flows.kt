package info.benjaminhill.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ChannelResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.sync.Semaphore
import java.io.File
import java.io.RandomAccessFile
import java.nio.charset.Charset
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.TimeMark
import kotlin.time.TimeSource.Monotonic


/**
 * From https://github.com/Kotlin/kotlinx.coroutines/compare/master...lowasser:map-concurrent?diff=split
 *
 * Returns a flow with elements equal to `map(f)`, including having the same order, but evaluates up
 * to `maxConcurrency` of the flow values concurrently, up to a limit of `buffer` elements ahead of
 * the consumer.
 *
 * For example, `flowOf(a, b, c, d, e).mapConcurrently(maxConcurrency = 3, buffer = 4, f)` will
 * evaluate `f(a)`, `f(b)`, and `f(c)` concurrently, and will start evaluating `f(d)` as soon as
 * one of those complete, but `f(e)` will not start until `f(a)` is collected.
 *
 * If `x` is emitted by the backing flow and `f(x)` throws an exception, the returned flow
 * will attempt to cancel the evaluation of `f` on any values emitted after `x`, but will continue
 * evaluating `f` on values emitted before `x`.  So in the above example, if `f(b)` throws before
 * `f(a)` or `f(c)` complete, `f(c)` will be cancelled but `f(a)` will be allowed to complete.
 */
fun <T, R> Flow<T>.mapConcurrently(
    maxConcurrency: Int = (Runtime.getRuntime().availableProcessors() - 1).coerceAtLeast(1),
    buffer: Int = maxConcurrency * 2,
    f: suspend (T) -> R
): Flow<R> {
    require(maxConcurrency > 0) { "Expected maxConcurrency to be > 0 but was $maxConcurrency" }
    require(buffer > 1) { "Expected buffer to be > 1 but was $buffer" }
    return flow {
        /*
         * This has lots of moving parts, unfortunately, so here's a sketch of what's going on.
         *
         * First, the semaphore controls concurrency on evaluating f *and* on getting upstream elements.
         * So in a flow emitting a, b, c with maxConcurrency = 3, there can be three concurrent tasks:
         * computing f(a) and f(b) and getting c from upstream.  Thus, we acquire permits *before*
         * collecting the element they're associated with, including before the first element,
         * and release them after computing f on that element.
         *
         * The relationship between collecting the upstream flow and launching the downstream results
         * is unusual, because an exception in f should cancel its "parent," the upstream flow
         * collection, but not all of its siblings, since prior elements may still be in progress.
         * Normally arranged coroutine scopes simply won't permit that, so we have to make the upstream
         * flow collection a sibling task of evaluating f on its elements, requiring a separate channel
         * and collection job.
         *
         * To achieve the effect that f(x) cancels f on values that come after x, but not values that
         * came before, we maintain an implicit linked list of CompletableDeferreds.
         * exceptionWasThrownEarlier completes exceptionally if f threw on any element
         * "before this one," and exceptionWasThrownEarlierOrHere completes exceptionally if f threw
         * on any element before this one, or on this one; we install completion handlers that propagate
         * appropriately.
         */
        val semaphore = Semaphore(permits = maxConcurrency, acquiredPermits = 1)

        supervisorScope {
            val channel = Channel<T>(0)
            val upstreamJob = launch {
                val upstreamCollectExceptionOrNull = runCatching {
                    collect {
                        channel.send(it)
                        semaphore.acquire()
                    }
                }.exceptionOrNull()
                channel.close(upstreamCollectExceptionOrNull)
            }

            var exceptionWasThrownEarlier = CompletableDeferred<Nothing>()
            while (true) {
                val tResult = try {
                    select<ChannelResult<T>> {
                        channel.onReceiveCatching { it }
                        exceptionWasThrownEarlier.onAwait { it } // throws the exception
                    }
                } catch (thrown: Throwable) {
                    upstreamJob.cancel(thrown.asCancellation())
                    break
                }
                if (tResult.isClosed) {
                    val ex = tResult.exceptionOrNull()
                    if (ex != null) {
                        emit(async { throw ex })
                    }
                    break
                }
                val t = tResult.getOrThrow()

                // Deferred that will be completed exceptionally if evaluating f on any value before t, or
                // on t itself, threw.
                val exceptionWasThrownEarlierOrHere = CompletableDeferred<Nothing>()

                val evalF = async { f(t) }
                evalF.invokeOnCompletion { thrown ->
                    if (thrown != null) {
                        exceptionWasThrownEarlierOrHere.completeExceptionally(thrown)
                    } else {
                        semaphore.release()
                    }
                }
                exceptionWasThrownEarlier.invokeOnCompletion { thrown -> // should never be null
                    // don't nest CancellationExceptions arbitrarily deep
                    evalF.cancel(thrown!!.asCancellation())

                    // it's possible that evalF completed successfully, but there are other downstream f's to
                    // cancel, so we can't depend on the evalF completion handler to propagate thrown
                    exceptionWasThrownEarlierOrHere.completeExceptionally(thrown)
                }
                emit(evalF)
                exceptionWasThrownEarlier = exceptionWasThrownEarlierOrHere
            }
        }
    }
        .buffer(if (buffer == Int.MAX_VALUE) buffer else buffer - 2)
        // one async can be started but unbuffered, and one can be awaiting; the -2 is necessary to
        // ensure exactly what the doc describes
        .map { it.await() }
}

private fun Throwable.asCancellation(): CancellationException =
    this as? CancellationException ?: CancellationException(null, this)


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
@OptIn(ExperimentalTime::class)
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