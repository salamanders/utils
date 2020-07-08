package info.benjaminhill.utils

import java.util.concurrent.atomic.AtomicLong
import kotlin.time.ExperimentalTime
import kotlin.time.nanoseconds

/**
 * Spit out println log lines every hitFrequency calls to hit()
 * TODO: Every X duration (instead of every X hits)
 */
class CountHits(
    private val hitFrequency: Int = 5_000,
    private val logLine: (perSec: Int) -> String = { perSec: Int -> "Running at $perSec/sec" }
) {

    @ExperimentalTime
    private var startTimeNs = System.nanoTime().nanoseconds
    private var hitCount = AtomicLong()

    @ExperimentalTime
    fun hit() {
        if (hitCount.incrementAndGet() >= hitFrequency) {
            val nowNs = System.nanoTime().nanoseconds
            val elapsedTime = nowNs - startTimeNs
            val hitPerSecond = hitCount.toDouble() / elapsedTime.inSeconds
            println(logLine(hitPerSecond.toInt()))
            hitCount.set(0)
            startTimeNs = nowNs
        }
    }
}