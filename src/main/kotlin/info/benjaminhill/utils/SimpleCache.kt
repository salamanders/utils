package info.benjaminhill.utils

import mu.KotlinLogging
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import kotlin.time.*

private val logger = KotlinLogging.logger {}

/**
 * To cache a lot of small function calls to disk.
 * Only one app can use it at a time.
 */
@ExperimentalTime
class SimpleCache<K : Serializable, V : Serializable>(
    private val cacheFile: File = File("simplecache.ser.gz"),
    private val persistEveryWrites: Int = 1_000,
    private val persistEveryDuration: Duration = 3.minutes
) {
    private val cache: MutableMap<K, V> = ConcurrentHashMap()
    private val mutationCount: AtomicLong = AtomicLong()
    private val lastPersisted: AtomicReference<TimeMark> = AtomicReference(TimeSource.Monotonic.markNow())

    init {
        if (cacheFile.exists() && cacheFile.canRead() && cacheFile.length() > 0) {
            ObjectInputStream(GZIPInputStream(cacheFile.inputStream())).use { inputStream ->
                @Suppress("UNCHECKED_CAST")
                cache.putAll(inputStream.readObject() as Map<K, V>)
                logger.debug { "SimpleCache startup loaded ${cache.size}" }
            }
        }
    }

    /**
     * Ok to call manually, but better practice to let the parameters do the persisting.
     */
    fun persist() {
        ObjectOutputStream(GZIPOutputStream(cacheFile.outputStream())).use {
            it.writeObject(cache)
            logger.debug { "SimpleCache auto-persisted ${cache.size}" }
        }
        mutationCount.set(0)
        lastPersisted.set(TimeSource.Monotonic.markNow())
    }

    val size: Int
        get() = cache.size

    /**
     * Call with typical map format: `myCache[a] = b`
     */
    operator fun set(key: K, value: V) {
        this.cache[key] = value
        if (
            mutationCount.incrementAndGet() >= persistEveryWrites ||
            lastPersisted.get().elapsedNow() >= persistEveryDuration
        ) {
            persist()
        }
    }

    operator fun get(key: K) = this.cache[key]

    /** Load a cached object if available, calculate and cache if not.
     * Call using myCache(a) { b }*/
    operator fun invoke(key: K, exec: () -> V): V {
        if (!cache.containsKey(key)) {
            set(key, exec())
            logger.trace { "SimpleCache miss on '$key'" }
        } else {
            logger.trace { "SimpleCache hit on '$key'" }
        }
        return cache[key]!!
    }
}